package com.example.startracker.alignment.tiltalignment

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
import com.example.startracker.databinding.FragmentTiltAlignmentBinding

class TiltAlignmentFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentTiltAlignmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_tilt_alignment, container, false
        )

        val application = requireNotNull(this.activity).application

        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao

        val viewModelFactory = TiltAlignmentViewModelFactory(dataSource, application)

        val tiltAlignmentViewModel = ViewModelProvider(this, viewModelFactory).get(
            TiltAlignmentViewModel::class.java
        )

        binding.tiltAlignmentViewModel = tiltAlignmentViewModel

        binding.lifecycleOwner = this

        val redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        val greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        val whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.okButton.setBackgroundColor(redButtonColor)
        binding.okButton.setTextColor(whiteTextColor)

        binding.okButton.setOnClickListener(){
            this.findNavController().navigate(R.id.action_tiltAlignmentFragment_to_endAligmentFragment)
        }

        return binding.root
    }

}