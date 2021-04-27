package com.example.StarTracker.alignment.polarAlignment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.StarTracker.R

class PolarAlignmentFragment : Fragment() {

    companion object {
        fun newInstance() = PolarAlignmentFragment()
    }

    private lateinit var viewModel: PolarAlignmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_polar_alignment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PolarAlignmentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}