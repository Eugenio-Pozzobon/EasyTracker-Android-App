package com.example.StarTracker.alignment.endalignment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.StarTracker.R

class EndAligmentFragment : Fragment() {

    companion object {
        fun newInstance() = EndAligmentFragment()
    }

    private lateinit var viewModel: EndAligmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_end_aligment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EndAligmentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}