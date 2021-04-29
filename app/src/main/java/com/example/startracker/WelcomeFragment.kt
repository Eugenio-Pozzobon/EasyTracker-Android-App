package com.example.startracker

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.startracker.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        binding.initbutton.setOnClickListener{ v: View ->
            v.findNavController().navigate(R.id.action_welcomeFragment_to_newProfileFragment)
        }

        val redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        val whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.initbutton.setBackgroundColor(redButtonColor)
        binding.initbutton.setTextColor(whiteTextColor)

        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        (activity as MainActivity?)?.setDrawer_locked()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

