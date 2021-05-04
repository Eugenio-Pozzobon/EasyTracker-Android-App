package com.example.startracker.currentprofile

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.startracker.R
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentCurrentProfileBinding


class CurrentProfileFragment : Fragment() {

    lateinit var currentProfileViewModel: CurrentProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // make the data binding for this fragment
        val binding: FragmentCurrentProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_current_profile, container, false
        )

        val application = requireNotNull(this.activity).application

        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao

        val viewModelFactory = CurrentProfileViewModelFactory(dataSource, application)

        currentProfileViewModel = ViewModelProvider(this, viewModelFactory).get(
            CurrentProfileViewModel::class.java
        )

        binding.currentProfileViewModel = currentProfileViewModel

        binding.lifecycleOwner = this

        val redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        val greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        val whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        //handle bluetooth connection. Change buttons colors if was connected.
        currentProfileViewModel.onConnected.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                binding.buttonConnect.setBackgroundColor(redButtonColor)
            } else {
                binding.buttonConnect.setBackgroundColor(greenButtonColor)
                binding.buttonStartAlignment.setBackgroundColor(greenButtonColor)
            }
        })

        //handleif user want to start alignment process
        currentProfileViewModel.screenChange.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                currentProfileViewModel.doneOnChangeScreen()
                this.findNavController()
                    .navigate(R.id.action_currentProfileFragment_to_levelAlignmentFragment)
                binding.buttonConnect.setBackgroundColor(redButtonColor)
                binding.buttonStartAlignment.setBackgroundColor(redButtonColor)
            }
        })

        //rename the view fragment as the name of the selected profile
        currentProfileViewModel.profileName.observe(viewLifecycleOwner, {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = it
        })

        //check if exists an last seleted profile, if not, put the user and load profile screen
        currentProfileViewModel.noLastProfileAvailable.observe(viewLifecycleOwner, {
            if (it == true) {
                currentProfileViewModel.doneOnChangeScreen()
                this.findNavController()
                    .navigate(R.id.action_currentProfileFragment_to_loadProfilesFragment)
            }
        })

        //check if is a new user, so then put at the welcome screen
        currentProfileViewModel.newUserDetected.observe(viewLifecycleOwner, {
            if (it == true) {
                currentProfileViewModel.doneOnChangeScreen()
                this.findNavController()
                    .navigate(R.id.action_currentProfileFragment_to_welcomeFragment)
            }
        })

        //change buttons and text colors
        binding.buttonConnect.setBackgroundColor(redButtonColor)
        binding.buttonConnect.setTextColor(whiteTextColor)

        binding.buttonStartAlignment.setBackgroundColor(redButtonColor)
        binding.buttonStartAlignment.setTextColor(whiteTextColor)


        setHasOptionsMenu(true)
        return binding.root

    }

    //inflate the overflow menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    //handle the user selection at overflow menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) || super.onOptionsItemSelected(
            item
        )
    }

}