package com.example.startracker.alignment.endalignment

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.startracker.MainActivity
import com.example.startracker.R
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentEndAlignmentBinding
import kotlin.properties.Delegates

class EndAlignmentFragment : Fragment() {

    var redButtonColor by Delegates.notNull<Int>()
    var greenButtonColor by Delegates.notNull<Int>()
    var whiteTextColor by Delegates.notNull<Int>()

    lateinit var endAlignmentViewModel:EndAlignmentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        val binding: FragmentEndAlignmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_end_alignment, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao
        val viewModelFactory = EndAlignmentViewModelFactory(dataSource, application)

        endAlignmentViewModel = ViewModelProvider(this, viewModelFactory).get(
            EndAlignmentViewModel::class.java
        )
        binding.endAlignmentViewModel = endAlignmentViewModel
        binding.lifecycleOwner = this

        redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)


        setButtonEnable(binding.startTrackingButton)
        binding.startTrackingButton.setOnClickListener(){
            setButtonDisable(binding.startTrackingButton)
            setButtonEnable(binding.endTrackingButton)
        }


        setButtonDisable(binding.endTrackingButton)
        binding.endTrackingButton.setOnClickListener(){
            setButtonEnable(binding.startTrackingButton)
            setButtonDisable(binding.endTrackingButton)
            this.findNavController().navigate(R.id.action_endAligmentFragment_to_currentProfileFragment)
        }


        setHasOptionsMenu(true)
        return binding.root
    }
    private fun setButtonEnable(button: Button){
        button.setBackgroundColor(greenButtonColor)
        button.setTextColor(whiteTextColor)
        button.isEnabled = true
    }

    private fun setButtonDisable(button: Button){
        button.setBackgroundColor(redButtonColor)
        button.setTextColor(whiteTextColor)
        button.isEnabled = false
    }

    // Check if the device has bluetooth and return if it is enable.
    // If not, call an Intent that is handle in onActivityResult()
    private val REQUEST_ENABLE_BT = 1
    private fun startBluetooth(): Boolean {

        var btOperationState = false
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            val btAdapter = BluetoothAdapter.getDefaultAdapter()
            if (btAdapter.isEnabled) {
                btOperationState = true
            } else {
                //turn on bluetooth
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }

        }
        return btOperationState
    }

    // This function is android based for review an Activity intent.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    reconnect()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // if bluetooth is turn on, make an reconnection.
    private fun reconnect() {
        if (startBluetooth()) {
            (activity as MainActivity).hc05.reconnect()
        }
    }

    // inflate overflow menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu_endalignment, menu)
    }

    // handler options of overflow menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.reconnect) {
            reconnect()
            return true
        }
        if (item.itemId == R.id.currentProfileFragment) {
            this.findNavController().navigate(R.id.action_endAligmentFragment_to_currentProfileFragment)
            return true
        }
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
                super.onOptionsItemSelected(item)
    }

}