package com.example.startracker.alignment.endalignment

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
import com.example.startracker.databinding.FragmentEndAlignmentBinding

class EndAlignmentFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentEndAlignmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_end_alignment, container, false
        )

        val application = requireNotNull(this.activity).application

        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao

        val viewModelFactory = EndAlignmentViewModelFactory(dataSource, application)

        val endAlignmentViewModel = ViewModelProvider(this, viewModelFactory).get(
            EndAlignmentViewModel::class.java
        )

        binding.endAlignmentViewModel = endAlignmentViewModel

        binding.lifecycleOwner = this

        val redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        val greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        val whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.okButton.setBackgroundColor(redButtonColor)
        binding.okButton.setTextColor(whiteTextColor)

        binding.okButton.setOnClickListener(){
            this.findNavController().navigate(R.id.action_endAligmentFragment_to_currentProfileFragment)
        }

        return binding.root
    }

}