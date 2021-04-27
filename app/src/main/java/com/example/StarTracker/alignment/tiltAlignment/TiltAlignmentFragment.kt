package com.example.StarTracker.alignment.tiltAlignment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.StarTracker.R

class TiltAlignmentFragment : Fragment() {

    companion object {
        fun newInstance() = TiltAlignmentFragment()
    }

    private lateinit var viewModel: TiltAlignmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tilt_alignment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TiltAlignmentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}