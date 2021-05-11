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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.startracker.MainActivity
import com.example.startracker.R
import com.example.startracker.convertDpToPixel
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentLevelAlignmentBinding
import com.example.startracker.mapFloat
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.Math.ceil
import java.lang.Math.floor
import kotlin.math.ceil
import kotlin.math.floor

class LevelAlignmentFragment : Fragment() {

    private var circleMarginX:Float = 0F
    private var circleMarginY:Float = 0F

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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
        var getTime: Long = System.currentTimeMillis()
        (activity as MainActivity).hc05.updatedHandle.observeForever {
            updateAlignment()
            binding.circleAlignment.translationY = convertDpToPixel(circleMarginY,requireContext())
            binding.circleAlignment.translationX = convertDpToPixel(circleMarginX,requireContext())
            print(it.toString() + " " + circleMarginY.toString() + circleMarginX.toString())
            print("  timing: ")
            println(System.currentTimeMillis()-getTime)
            getTime = System.currentTimeMillis()

        }

        return binding.root
    }

    private fun updateAlignment(){
        try {
            val pitch: Float? = (activity as MainActivity).hc05.dataPitch.value
            val roll: Float? = (activity as MainActivity).hc05.dataRoll.value

            val valueMax: Float = 90F
            val valueMin: Float = -90F

            val paddingMax: Float = 115.0F
            val paddingMin: Float = -115.0F

            circleMarginX = mapFloat(-pitch!!, valueMin, valueMax, paddingMin, paddingMax)
            circleMarginY = mapFloat(roll!!, valueMin, valueMax, paddingMin, paddingMax)
        }catch (e: Exception){
            circleMarginX = 0F
            circleMarginY = 0F
        }
    }
}