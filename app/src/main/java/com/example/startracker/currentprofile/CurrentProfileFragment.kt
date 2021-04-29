package com.example.startracker.currentprofile

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.startracker.R
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentCurrentProfileBinding
import com.example.startracker.databinding.FragmentNewProfileBinding
import com.example.startracker.newprofile.NewProfileViewModel
import com.example.startracker.newprofile.NewProfileViewModelFactory

class CurrentProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentCurrentProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_current_profile, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao

        val viewModelFactory = CurrentProfileViewModelFactory(dataSource, application)

        val currentProfileViewModel = ViewModelProvider(this, viewModelFactory).get(CurrentProfileViewModel::class.java)

        binding.currentProfileViewModel = currentProfileViewModel

        binding.lifecycleOwner = this

        currentProfileViewModel.onConnected.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                binding.buttonConnect.setBackgroundColor(getResources().getColor(R.color.red_button));
            }else{
                binding.buttonConnect.setBackgroundColor(getResources().getColor(R.color.green_button));
                binding.buttonStartAlignment.setBackgroundColor(getResources().getColor(R.color.green_button));
            }
        })

        currentProfileViewModel.screenChange.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                currentProfileViewModel.doneOnChangeScreen()
                this.findNavController().navigate(R.id.action_currentProfileFragment_to_levelAlignmentFragment)
                binding.buttonConnect.setBackgroundColor(getResources().getColor(R.color.red_button));
                binding.buttonStartAlignment.setBackgroundColor(getResources().getColor(R.color.red_button))
            }
        })

        binding.buttonConnect.setBackgroundColor(getResources().getColor(R.color.red_button));
        binding.buttonConnect.setTextColor(getResources().getColor(R.color.white));

        binding.buttonStartAlignment.setBackgroundColor(getResources().getColor(R.color.red_button));
        binding.buttonStartAlignment.setTextColor(getResources().getColor(R.color.white));

        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

}