package com.example.startracker.debug

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.startracker.databinding.FragmentDebugBinding


class DebugFragment : Fragment() {
    private var _binding: FragmentDebugBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding
    get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentDebugBinding.inflate(inflater, container, false)
        return binding.root
        //Todo: DEBUG FRAGMENT FOR BLUETOOTH!
    }
}