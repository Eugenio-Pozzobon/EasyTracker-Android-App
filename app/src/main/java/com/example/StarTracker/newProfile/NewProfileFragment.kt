package com.example.StarTracker.newProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.StarTracker.R
import com.example.StarTracker.databinding.FragmentNewProfileBinding

class NewProfileFragment : Fragment() {

    private var _binding: FragmentNewProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: NewProfileViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewProfileBinding.inflate(inflater, container, false)

        binding.buttonConnect.setOnClickListener{ v: View ->
            v.findNavController().navigate(R.id.action_newProfileFragment_to_currentProfileFragment)
        }

        binding.buttonConnect.setBackgroundColor(getResources().getColor(R.color.red_button));
        binding.buttonConnect.setTextColor(getResources().getColor(R.color.black));

        val view = binding.root
        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(NewProfileViewModel::class.java)
        // TODO: Use the ViewModel

//        if(true){
//            Log.i("CUSTOM TAG", "activiy")
//            (activity as MainActivity).makeHomeStart()
//            val navOptions = NavOptions.Builder()
//                .setPopUpTo(R.id.newProfileFragment, true)
//                .build()
//            requireView().findNavController().navigate(R.id.action_newProfileFragment_to_currentProfileFragment, null, navOptions)
//        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}