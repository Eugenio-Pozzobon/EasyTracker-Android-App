package com.example.StarTracker.alignment.levelAlignment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.StarTracker.R

class LevelAlignmentFragment : Fragment() {

    companion object {
        fun newInstance() = LevelAlignmentFragment()
    }

    private lateinit var viewModelLevel: LevelAlignmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alignment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelLevel = ViewModelProvider(this).get(LevelAlignmentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}