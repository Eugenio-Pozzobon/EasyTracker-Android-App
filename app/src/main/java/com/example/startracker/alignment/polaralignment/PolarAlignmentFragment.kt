package com.example.startracker.alignment.polaralignment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.startracker.MainActivity
import com.example.startracker.R
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentPolarAlignmentBinding
import com.example.startracker.databinding.FragmentTiltAlignmentBinding
import com.google.android.material.snackbar.Snackbar

class PolarAlignmentFragment : Fragment() {

    private var redButtonColor: Int = 0
    private var greenButtonColor: Int = 0
    private var whiteTextColor: Int = 0

    private var rotate:Float = 0F

    lateinit var btSnack: Snackbar
    lateinit var binding: FragmentPolarAlignmentBinding
    lateinit var polarAlignmentViewModel:PolarAlignmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_polar_alignment, container, false
        )

        val application = requireNotNull(this.activity).application

        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao

        val viewModelFactory = PolarAlignmentViewModelFactory(dataSource, application)

        polarAlignmentViewModel = ViewModelProvider(this, viewModelFactory).get(
            PolarAlignmentViewModel::class.java
        )

        binding.polarAlignmentViewModel = polarAlignmentViewModel

        binding.lifecycleOwner = this

        redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.okButton.setBackgroundColor(redButtonColor)
        binding.okButton.setTextColor(whiteTextColor)

        binding.okButton.setOnClickListener(){
            this.findNavController().navigate(R.id.action_polarAlignmentFragment_to_tiltAlignmentFragment)
        }

        (activity as MainActivity).hc05.updatedHandle.observeForever(handlerUpdateObserver)
        (activity as MainActivity).hc05.mmIsConnected.observeForever(checkConnection)

        setHasOptionsMenu(true)
        
        return binding.root
    }


    private val checkConnection = Observer<Boolean?> {
        try {
            if (it != true) {
                try {
                    btSnack = Snackbar.make(
                        requireView(),
                        getString(R.string.fail_connection),
                        Snackbar.LENGTH_LONG,
                    )
                    btSnack.setAction(getString(R.string.bt_snack_action)) {
                        (activity as MainActivity).hc05.reconnect(polarAlignmentViewModel.bluetoothMac.value.toString())
                    }
                    btSnack.show()
                }catch (e: java.lang.Exception){
                    Log.e("SNACKBARDEBUG", "SNACKBAR PROBLEM", e)
                }
            }else if(it == true){
                try {
                    btSnack = Snackbar.make(
                        requireView(),
                        getString(R.string.done_connection),
                        Snackbar.LENGTH_SHORT,
                    )
                    btSnack.show()
                }catch (e: java.lang.Exception){
                    Log.e("SNACKBARDEBUG", "SNACKBAR PROBLEM", e)
                }
            }
        } catch (e: Exception) {
            Log.e("DEBUGCONNECTION", "Observer in Level Alignment not killed", e)
        }
    }

    private val handlerUpdateObserver = Observer<Boolean> {
        try {
            updateAlignment()
            binding.compass.rotation = rotate
        } catch (e: Exception) {
            Log.e("DEBUGCONNECTION", "Observer in Level Alignment not killed", e)
        }
    }

    private fun updateAlignment() {
        try {
            val yaw: Float? = (activity as MainActivity).hc05.dataYaw.value


            if (((yaw!! <= 0.2) && (yaw >= -0.2))) {
                binding.okButton.setBackgroundColor(greenButtonColor)
            } else {
                binding.okButton.setBackgroundColor(redButtonColor)
            }

            rotate = yaw
        } catch (e: Exception) {
            rotate = 0F
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu_compass, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.reconnect) {
            (activity as MainActivity).hc05.reconnect(polarAlignmentViewModel.bluetoothMac.value.toString())
            return true
        }

        if(item.itemId == R.id.calibrate_compass) {
            return true
        }

        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
                super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).hc05.updatedHandle.removeObserver(handlerUpdateObserver)
        (activity as MainActivity).hc05.mmIsConnected.removeObserver(checkConnection)
    }

}