package com.example.StarTracker.alignment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.StarTracker.R

class AlignmentFragment : Fragment() {

    companion object {
        fun newInstance() = AlignmentFragment()
    }

    private lateinit var viewModel: AlignmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alignment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AlignmentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}