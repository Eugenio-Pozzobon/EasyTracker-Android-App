package com.example.startracker.newprofile

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.startracker.R
import com.example.startracker.alignment.endalignment.EndAligmentViewModel
import com.example.startracker.databinding.FragmentNewProfileBinding
import com.example.startracker.database.ProfileDatabase

class NewProfileFragment : Fragment() {

//    private var _binding: FragmentNewProfileBinding? = null
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        // Inflate the layout for this fragment
//        _binding = FragmentNewProfileBinding.inflate(inflater, container, false)
        val binding: FragmentNewProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_new_profile, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao

        val viewModelFactory = NewProfileViewModelFactory(dataSource, application)

        val newProfileViewModel = ViewModelProvider(this, viewModelFactory).get(NewProfileViewModel::class.java)
        //newProfileViewModel = ViewModelProvider(this).get(NewProfileViewModel::class.java)

        binding.newProfileViewModel = newProfileViewModel

        binding.lifecycleOwner = this

        newProfileViewModel.onConnected.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                newProfileViewModel.doneOnChangeScreen()
                this.findNavController().navigate(R.id.action_newProfileFragment_to_currentProfileFragment)
            }
        })

        // TODO: Add get localization
        // TODO: Add overflow fragment for question button


        setHasOptionsMenu(true)

        binding.buttonConnect.setBackgroundColor(getResources().getColor(R.color.red_button));
        binding.buttonConnect.setTextColor(getResources().getColor(R.color.white));

        val view = binding.root
        return view

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.overflow_menu_np, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val optionsBuilder = NavOptions.Builder()
        optionsBuilder
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out);

        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}