package com.example.startracker.newprofile

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.startracker.R
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentNewProfileBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class NewProfileFragment : Fragment() {

//    private var _binding: FragmentNewProfileBinding? = null
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationPermissionCode = 1

    lateinit var newProfileViewModel:NewProfileViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
//        // Inflate the layout for this fragment
        val binding: FragmentNewProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_new_profile, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao

        val viewModelFactory = NewProfileViewModelFactory(dataSource, application)

        newProfileViewModel = ViewModelProvider(this, viewModelFactory).get(NewProfileViewModel::class.java)
        //newProfileViewModel = ViewModelProvider(this).get(NewProfileViewModel::class.java)

        binding.newProfileViewModel = newProfileViewModel

        binding.lifecycleOwner = this

        newProfileViewModel.onConnected.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                newProfileViewModel.doneOnChangeScreen()
                this.findNavController().navigate(R.id.action_newProfileFragment_to_currentProfileFragment)
            }
        })

        newProfileViewModel.setNameError.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                binding.textProfileName.setError("*")
                binding.textProfileName.requestFocus()
            }else{
                binding.textProfileName.error = null
                binding.textProfileName.clearFocus()
            }
        })

        newProfileViewModel.setGpsDataError.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                binding.gpsNumber.error = "*"
                binding.gpsNumber.requestFocus()
            }else{
                binding.gpsNumber.error = null
                binding.gpsNumber.clearFocus()
            }
        })

        newProfileViewModel.setMagDeclinationError.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                binding.declinationNumber.error = "*"
                binding.declinationNumber.requestFocus()
            }else{
                binding.declinationNumber.error = null
                binding.declinationNumber.clearFocus()
            }
        })

        setHasOptionsMenu(true)

        val redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        //val greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        val whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.buttonConnect.setBackgroundColor(redButtonColor)
        binding.buttonConnect.setTextColor(whiteTextColor)

        binding.imageQuestionVector.setOnClickListener(){
            this.findNavController().navigate(R.id.action_newProfileFragment_to_howToUseFragment)
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.imageGpsCircle.setOnClickListener(){
            getLocation()
        }

        val view = binding.root
        return view

    }

    private fun getLocation(){
        if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }else{
            Log.i("DEBUGLOCATION","GPS LOCATION")

            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        newProfileViewModel.updateGps(location.latitude.toString())
                    }
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu_newprofile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return NavigationUI.onNavDestinationSelected(
                item,
                requireView().findNavController()
            ) || super.onOptionsItemSelected(item)
    }

}