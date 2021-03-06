package com.epp.easytracker.newprofile

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.hardware.GeomagneticField
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.epp.easytracker.R
import com.epp.easytracker.database.ProfileDatabase
import com.epp.easytracker.databinding.FragmentNewProfileBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlin.properties.Delegates


class NewProfileFragment : Fragment() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationPermissionCode = 1

    lateinit var newProfileViewModel:NewProfileViewModel

    lateinit var btAdapter: BluetoothAdapter
    private val REQUEST_ENABLE_BT=1

    private var whiteColor by Delegates.notNull<Int>()
    private var blackColor by Delegates.notNull<Int>()
    private var greyBoldColor by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding: FragmentNewProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_new_profile, container, false
        )

        // crate and start view model and it variables.
        val application = requireNotNull(this.activity).application
        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao
        val viewModelFactory = NewProfileViewModelFactory(dataSource, application)

        newProfileViewModel = ViewModelProvider(this, viewModelFactory)
            .get(NewProfileViewModel::class.java)
        binding.newProfileViewModel = newProfileViewModel
        binding.lifecycleOwner = this

        //handle if the user didn't filled name text input
        newProfileViewModel.setNameError.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                binding.textProfileName.setError("*")
                binding.textProfileName.requestFocus()
            } else {
                binding.textProfileName.error = null
                binding.textProfileName.clearFocus()
            }
        })

        //handle if the user didn't filled gps text input
        newProfileViewModel.setGpsDataError.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                binding.gpsNumber.error = "*"
                binding.gpsNumber.requestFocus()
            } else {
                binding.gpsNumber.error = null
                binding.gpsNumber.clearFocus()
            }
        })

        //handle if the user didn't filled declination text input
        newProfileViewModel.setMagDeclinationError.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                binding.declinationNumber.error = "*"
                binding.declinationNumber.requestFocus()
            } else {
                binding.declinationNumber.error = null
                binding.declinationNumber.clearFocus()
            }
        })

        binding.imageQuestionVector.setOnClickListener(){
            this.findNavController().navigate(R.id.action_newProfileFragment_to_howToUseFragment)
        }


        //change buttons colors as design guidelines
        val redButtonColor = ContextCompat.getColor(requireContext(), R.color.press_button)
        //val greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        whiteColor = ContextCompat.getColor(requireContext(), R.color.white)
        blackColor = ContextCompat.getColor(requireContext(), R.color.black)
        greyBoldColor = ContextCompat.getColor(requireContext(), R.color.grey_bold)


        //start animation drawable
        val imageView = binding.imageGpsCircle
        imageView.setBackgroundResource(R.drawable.gps_circle_animation)
        gpsAnimation = imageView.background as AnimatedVectorDrawable

        binding.buttonConnect.setBackgroundColor(redButtonColor)
        binding.buttonConnect.setTextColor(whiteColor)

        //get user current localization if clicked at marker button
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
            requireActivity()
        )

        binding.imageGpsCircle.setOnClickListener(){
            getLocation()
            countDownAnimated.onFinish()
            countDownAnimated.cancel()
            imageView.background = null
            imageView.setImageResource(R.drawable.gps_circle_vector)
        }

        newProfileViewModel.onConnected.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                newProfileViewModel.doneOnChangeScreen()
                //this.findNavController().navigate(R.id.action_newProfileFragment_to_currentProfileFragment)
                this.findNavController()
                    .navigate(R.id.action_newProfileFragment_to_pairedDevicesFragment)
            }
        })

        newProfileViewModel.startConnection.observe(viewLifecycleOwner, {

            if (it) {
                startBluetooth()
            }
        })

        setHasOptionsMenu(true)
        return binding.root

    }

    // Check if the device has bluetooth and return if it is enable.
    // If not, call an Intent that is handle in onActivityResult()
    private fun startBluetooth() {
        if(BluetoothAdapter.getDefaultAdapter() != null){
            btAdapter = BluetoothAdapter.getDefaultAdapter()

            if(!btAdapter.isEnabled){
                //turn on bluetooth
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }else{
                newProfileViewModel.onConnect()
            }
        }else{
            newProfileViewModel.onConnect()
            Log.e("DEBUGBLUETOOTH", "DONT HAVE BLUETOOTH ADAPTER")
        }
    }

    // This function is android based for review an Activity intent.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    newProfileViewModel.onConnect()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //check if user allow localization services an get current latitude of the smartphone
    private fun getLocation(){
        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }else{
            Log.i("DEBUGLOCATION", "GPS LOCATION")

            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val geoField = GeomagneticField(
                            location.latitude.toFloat(),
                            location.longitude.toFloat(),
                            location.altitude.toFloat(), System.currentTimeMillis()
                        )

                        newProfileViewModel.updateGps(location.latitude.toString())
                        newProfileViewModel.updateDeclination(geoField.declination.toString())
                        val gpsSnack = Snackbar.make(
                            requireView(),
                            getString(R.string.gps_verbose),
                            Snackbar.LENGTH_SHORT,
                        )
                        gpsSnack.setTextColor(Color.WHITE)
                        gpsSnack.show()
                    }
                }
        }
    }

    //inflate the overflow menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu_newprofile, menu)
    }

    //handle the user selection at overflow menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return NavigationUI.onNavDestinationSelected(
                item,
                requireView().findNavController()
            ) || super.onOptionsItemSelected(item)
    }

    private val countDownAnimated = object : CountDownTimer(3600 * 24 * 1000, 2550) {
        override fun onTick(millisUntilFinished: Long) {
            gpsAnimation.start()
            }
        override fun onFinish() {
        }
    }
    private lateinit var gpsAnimation:AnimatedVectorDrawable
    override fun onStart() {
        super.onStart()
        countDownAnimated.start()
        val gpsSnack = Snackbar.make(
            requireView(),
            getString(R.string.gps_marker_call),
            Snackbar.LENGTH_LONG,
        )
        gpsSnack.show()
    }

    override fun onPause() {
        super.onPause()
        countDownAnimated.onFinish()
        countDownAnimated.cancel()
    }
}