package com.example.startracker.alignment.endalignment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.startracker.R

class EndAlignmentFragment : Fragment() {

    companion object {
        fun newInstance() = EndAlignmentFragment()
    }

    private lateinit var viewModel: EndAlignmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_end_aligment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EndAlignmentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}