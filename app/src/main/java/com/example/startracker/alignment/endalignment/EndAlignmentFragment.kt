package com.example.startracker.alignment.endalignment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.startracker.R
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentEndAlignmentBinding
import kotlin.properties.Delegates

class EndAlignmentFragment : Fragment() {

    var redButtonColor by Delegates.notNull<Int>()
    var greenButtonColor by Delegates.notNull<Int>()
    var whiteTextColor by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

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

        redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)


        setButtonEnable(binding.startTrackingButton)
        binding.startTrackingButton.setOnClickListener(){
            setButtonDisable(binding.startTrackingButton)
            setButtonEnable(binding.endTrackingButton)
        }


        setButtonDisable(binding.endTrackingButton)
        binding.endTrackingButton.setOnClickListener(){
            setButtonEnable(binding.startTrackingButton)
            setButtonDisable(binding.endTrackingButton)
            this.findNavController().navigate(R.id.action_endAligmentFragment_to_currentProfileFragment)
        }

        return binding.root
    }
    private fun setButtonEnable(button: Button){
        button.setBackgroundColor(greenButtonColor)
        button.setTextColor(whiteTextColor)
        button.isEnabled = true
    }

    private fun setButtonDisable(button: Button){
        button.setBackgroundColor(redButtonColor)
        button.setTextColor(whiteTextColor)
        button.isEnabled = false
    }


}