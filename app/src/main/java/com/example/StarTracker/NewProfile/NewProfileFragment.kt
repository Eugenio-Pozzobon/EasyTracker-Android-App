package com.example.StarTracker.NewProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.StarTracker.R
import com.example.StarTracker.databinding.FragmentNewProfileBinding
import com.example.android.trackmysleepquality.database.ProfileDatabase

class NewProfileFragment : Fragment() {

    private var _binding: FragmentNewProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewProfileBinding.inflate(inflater, container, false)
//
//
//        val application = requireNotNull(this.activity).application
//
//        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao
//
//        val viewModelFactory = NewProfileViewModelFactory(dataSource, application)
//
//        val newProfileViewModel =
//                ViewModelProvider(
//                        this, viewModelFactory).get(NewProfileViewModel::class.java)
//
//        //binding.newProfileViewModel = newProfileViewModel
//
//        //binding.lifecycleOwner = this
//
//        // Inflate the layout for this fragment

        binding.buttonConnect.setOnClickListener{ v: View ->
            v.findNavController().navigate(R.id.action_newProfileFragment_to_currentProfileFragment)
        }

        binding.buttonConnect.setBackgroundColor(getResources().getColor(R.color.red_button));
        binding.buttonConnect.setTextColor(getResources().getColor(R.color.black));

        val view = binding.root
        return view

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}