package com.example.startracker.alignment.polaralignment

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
import com.example.startracker.databinding.FragmentPolarAlignmentBinding

class PolarAlignmentFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentPolarAlignmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_polar_alignment, container, false
        )

        val application = requireNotNull(this.activity).application

        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao

        val viewModelFactory = PolarAlignmentViewModelFactory(dataSource, application)

        val polarAlignmentViewModel = ViewModelProvider(this, viewModelFactory).get(
            PolarAlignmentViewModel::class.java
        )

        binding.polarAlignmentViewModel = polarAlignmentViewModel

        binding.lifecycleOwner = this

        val redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        val greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        val whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.textNorth.setBackgroundColor(whiteTextColor)
        binding.okButton.setBackgroundColor(redButtonColor)
        binding.okButton.setTextColor(whiteTextColor)

        binding.okButton.setOnClickListener(){
            this.findNavController().navigate(R.id.action_polarAlignmentFragment_to_tiltAlignmentFragment)
        }

        return binding.root
    }

}