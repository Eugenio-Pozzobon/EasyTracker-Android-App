package com.example.startracker.loadprofiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.startracker.R
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentLoadProfilesBinding

class LoadProfilesFragment : Fragment() {

    private lateinit var viewModel: LoadProfilesViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentLoadProfilesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_load_profiles, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao

        val viewModelFactory = LoadProfilesViewModelFactory(dataSource, application)

        val loadProfileViewModel = ViewModelProvider(this, viewModelFactory).get(LoadProfilesViewModel::class.java)

        binding.loadProfilesViewModel = loadProfileViewModel

        binding.lifecycleOwner = this

        val adapter = ProfileListAdapter(ProfileListener{
            profileId -> loadProfileViewModel.onProfileClicked(profileId)
        })

        val manager = LinearLayoutManager(activity)
        binding.profileList.layoutManager = manager
        binding.profileList.adapter = adapter

        loadProfileViewModel.profiles.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        val redButtonColor = ContextCompat.getColor(requireContext(), R.color.press_button)
        val greenButtonColor = ContextCompat.getColor(requireContext(), R.color.button_ok)
        val whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.buttonClear.setBackgroundColor(redButtonColor)
        binding.buttonClear.setTextColor(whiteTextColor)

        binding.buttonNew.setBackgroundColor(redButtonColor)
        binding.buttonNew.setTextColor(whiteTextColor)

        binding.buttonNew.setOnClickListener(){
            this.findNavController()
                .navigate(R.id.action_loadProfilesFragment_to_newProfileFragment)
        }

        loadProfileViewModel.navigateToEditProfile.observe(viewLifecycleOwner, {

            if(it == true) {
                this.findNavController()
                    .navigate(R.id.action_loadProfilesFragment_to_currentProfileFragment)
            }
        })

        loadProfileViewModel.clearButtonVisible.observe(viewLifecycleOwner, {
            if (it == false) { // Observed state is true.
                binding.buttonClear.setBackgroundColor(greenButtonColor)
                this.findNavController().navigate(R.id.action_loadProfilesFragment_to_newProfileFragment)
            }
        })


        return binding.root
    }

}