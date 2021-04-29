package com.example.startracker.loadprofiles

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.startracker.R
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentLoadProfilesBinding
import com.example.startracker.databinding.FragmentNewProfileBinding
import com.example.startracker.newprofile.NewProfileViewModel

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

//        val adapter = ProfileListAdapter()
//        binding.profilesList.adapter = adapter
//        loadProfileViewModel.profiles.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                adapter.submitList(it)
//            }
//        })


        val redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        val greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        val whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.buttonClear.setBackgroundColor(redButtonColor)
        binding.buttonClear.setTextColor(whiteTextColor)

        loadProfileViewModel.clearButtonVisible.observe(viewLifecycleOwner, {
            if (it == false) { // Observed state is true.
                binding.buttonClear.setBackgroundColor(greenButtonColor)
                this.findNavController().navigate(R.id.action_loadProfilesFragment_to_newProfileFragment)
            }
        })


        return binding.root
    }

}