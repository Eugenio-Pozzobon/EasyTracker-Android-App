package com.example.StarTracker.currentProfile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.StarTracker.R
import com.example.StarTracker.databinding.FragmentCurrentProfileBinding

class CurrentProfileFragment : Fragment() {

//    companion object {
//        fun newInstance() = CurrentProfileFragment()
//    }

    private lateinit var viewModel: CurrentProfileViewModel

    private var _binding: FragmentCurrentProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCurrentProfileBinding.inflate(inflater, container, false)


        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrentProfileViewModel::class.java)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.overflow_menu, menu)
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

}