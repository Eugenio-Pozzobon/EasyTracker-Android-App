package com.example.startracker.editprofile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.startracker.R
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentEditProfileBinding
import com.example.startracker.newprofile.EditProfileViewModel

class EditProfileFragment : Fragment() {

    private lateinit var editProfileViewModel: EditProfileViewModel

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



        val redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        val whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.buttonDelete.setBackgroundColor(redButtonColor)
        binding.buttonDelete.setTextColor(whiteTextColor)

        binding.buttonEdit.setBackgroundColor(redButtonColor)
        binding.buttonEdit.setTextColor(whiteTextColor)

        val view = binding.root
        return view
    }

}