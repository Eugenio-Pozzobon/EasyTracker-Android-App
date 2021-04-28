package com.example.startracker.loadprofiles

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.startracker.R
import com.example.startracker.databinding.FragmentLoadProfilesBinding
import com.example.startracker.databinding.FragmentNewProfileBinding

class LoadProfilesFragment : Fragment() {

    private lateinit var viewModel: LoadProfilesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentLoadProfilesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_load_profiles, container, false)

        val adapter = ProfileListAdapter()
        //binding.profilesList.adapter = adapter

        return binding.root
    }

}