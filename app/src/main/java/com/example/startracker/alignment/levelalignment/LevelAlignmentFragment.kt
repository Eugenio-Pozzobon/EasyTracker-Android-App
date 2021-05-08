package com.example.startracker.alignment.levelalignment

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.startracker.R
import com.example.startracker.convertDpToPixel
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentLevelAlignmentBinding
import java.lang.Math.ceil
import java.lang.Math.floor
import kotlin.math.ceil
import kotlin.math.floor

class LevelAlignmentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentLevelAlignmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_level_alignment, container, false
        )

        val application = requireNotNull(this.activity).application

        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao

        val viewModelFactory = LevelAlignmentViewModelFactory(dataSource, application)

        val levelAlignmentViewModel = ViewModelProvider(this, viewModelFactory).get(
            LevelAlignmentViewModel::class.java
        )

        binding.levelAlignmentViewModel = levelAlignmentViewModel

        binding.lifecycleOwner = this

        val redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        val greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        val whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.okButton.setBackgroundColor(redButtonColor)
        binding.okButton.setTextColor(whiteTextColor)

        binding.okButton.setOnClickListener(){
            this.findNavController().navigate(R.id.action_levelAlignmentFragment_to_polarAlignmentFragment)
        }

        levelAlignmentViewModel.updateAlignmentCheck.observe(viewLifecycleOwner,{
            if(it) {
                binding.circleAlignment.translationY = convertDpToPixel(levelAlignmentViewModel.marginX.value!!,requireContext())
                binding.circleAlignment.translationX = convertDpToPixel(levelAlignmentViewModel.marginY.value!!,requireContext())
                levelAlignmentViewModel.doneUpdateAlignment()
            }
        })



        return binding.root
    }
}