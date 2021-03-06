package com.epp.easytracker.editprofile

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.GeomagneticField
import android.location.Location
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.epp.easytracker.R
import com.epp.easytracker.database.ProfileDatabase
import com.epp.easytracker.databinding.FragmentEditProfileBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class EditProfileFragment : Fragment() {

    private lateinit var editProfileViewModel: EditProfileViewModel

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationPermissionCode = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentEditProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_profile, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao

        val viewModelFactory = EditProfileViewModelFactory(dataSource, application)

        editProfileViewModel = ViewModelProvider(this, viewModelFactory).get(EditProfileViewModel::class.java)
        //newProfileViewModel = ViewModelProvider(this).get(NewProfileViewModel::class.java)

        binding.editProfileViewModel = editProfileViewModel

        binding.lifecycleOwner = this

        editProfileViewModel.goToLoadProfiles.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                editProfileViewModel.doneOnChangeScreen()
                this.findNavController().navigate(R.id.action_editProfileFragment_to_loadProfilesFragment)
            }
        })
        editProfileViewModel.onEdit.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                editProfileViewModel.doneOnChangeScreen()
                this.findNavController().navigate(R.id.action_editProfileFragment_to_currentProfileFragment)
            }
        })

        //handle if the user didnt filled name text input
        editProfileViewModel.setNameError.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                binding.textProfileName.setError("*")
                binding.textProfileName.requestFocus()
            }else{
                binding.textProfileName.error = null
                binding.textProfileName.clearFocus()
            }
        })

        //handle if the user didnt filled gps text input
        editProfileViewModel.setGpsDataError.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                binding.gpsNumber.error = "*"
                binding.gpsNumber.requestFocus()
            }else{
                binding.gpsNumber.error = null
                binding.gpsNumber.clearFocus()
            }
        })

        //handle if the user didnt filled declination text input
        editProfileViewModel.setMagDeclinationError.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                binding.declinationNumber.error = "*"
                binding.declinationNumber.requestFocus()
            }else{
                binding.declinationNumber.error = null
                binding.declinationNumber.clearFocus()
            }
        })

        setHasOptionsMenu(true)

        //change buttons collors as design guidlines
        val redButtonColor = ContextCompat.getColor(requireContext(), R.color.press_button)
        val whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.buttonDelete.setBackgroundColor(redButtonColor)
        binding.buttonDelete.setTextColor(whiteTextColor)

        binding.buttonEdit.setBackgroundColor(redButtonColor)
        binding.buttonEdit.setTextColor(whiteTextColor)

        binding.imageQuestionVector.setOnClickListener(){
            this.findNavController().navigate(R.id.action_editProfileFragment_to_howToUseFragment)
        }

        //get user current localization if clicked at marker button
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.imageGpsCircle.setOnClickListener(){
            getLocation()
        }


        val view = binding.root
        return view
    }

    //check if user allow localization services anD get current latitude of the smartphone
    private fun getLocation(){
        if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }else{
            Log.i("DEBUGLOCATION","GPS LOCATION")

            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        val geoField = GeomagneticField(
                            location.latitude.toFloat(),
                            location.longitude.toFloat(),
                            location.altitude.toFloat(), System.currentTimeMillis())

                        editProfileViewModel.updateGps(location.latitude.toString())
                        editProfileViewModel.updateDeclination(geoField.declination.toString())
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

}