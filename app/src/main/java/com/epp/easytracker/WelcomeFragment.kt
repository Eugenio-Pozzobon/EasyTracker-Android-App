package com.epp.easytracker

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.epp.easytracker.MainActivity
import com.epp.easytracker.R
import com.epp.easytracker.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {

    lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_welcome, container, false
        )

        //navigate when user click in the init button
        binding.initbutton.setOnClickListener{ v: View ->
            v.findNavController().navigate(R.id.action_welcomeFragment_to_newProfileFragment)
        }

        val redButtonColor = ContextCompat.getColor(requireContext(), R.color.press_button)
        val whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.initbutton.setBackgroundColor(redButtonColor)
        binding.initbutton.setTextColor(whiteTextColor)

        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        //block user interaction with main nav menu
        (activity as MainActivity?)?.setDrawer_locked()

        //Todo: parear automagicamente
        //Todo: configurações - valores row nas telas

        return binding.root
    }

}

