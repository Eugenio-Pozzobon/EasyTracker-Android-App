package com.epp.easytracker.paireddevices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.epp.easytracker.R
import com.epp.easytracker.database.ProfileDatabase
import com.epp.easytracker.databinding.FragmentPairedDevicesBinding
import java.util.*


class PairedDevicesFragment : Fragment() {

    lateinit var binding: FragmentPairedDevicesBinding
    lateinit var pairedDevicesViewModel: PairedDevicesViewModel
    lateinit var btAdapter: BluetoothAdapter
    lateinit var btpairedDevices: Set<BluetoothDevice>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_paired_devices, container, false
        )

        // create view model
        val application = requireNotNull(this.activity).application
        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao
        val viewModelFactory = PairedDevicesViewModelFactory(dataSource, application)

        pairedDevicesViewModel =
            ViewModelProvider(this, viewModelFactory).get(PairedDevicesViewModel::class.java)
        binding.lifecycleOwner = this

        //check if user has bluetooth, if yes,
        // get a list of bluetooth paired devices and show this in an list with List Adapter
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            btAdapter = BluetoothAdapter.getDefaultAdapter()
            btpairedDevices = btAdapter.bondedDevices
            val listDevices: ArrayList<BluetoothDevice> = ArrayList()
            val listDevicesNames: ArrayList<String> = ArrayList()
            if (!btpairedDevices.isEmpty()) {
                for (device: BluetoothDevice in btpairedDevices) {
                    listDevices.add(device)
                    listDevicesNames.add(device.name)
                    Log.i("DEBUGAPP", "" + device)
                }
            } else {
                Toast.makeText(context, "No Paired Devices", Toast.LENGTH_LONG).show()
            }

            // create an Array Adapter with simple list item
            val adapter =
                context?.let {
                    ArrayAdapter(
                        it,
                        android.R.layout.simple_list_item_1,
                        listDevicesNames
                    )
                }

            //bind adapter and add a click listener for any item
            binding.deviceList.adapter = adapter
            binding.deviceList.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    val device: BluetoothDevice = listDevices[position]
                    val address: String = device.address
                    pairedDevicesViewModel.updateLastProfileWithBluetooth(address)
                }

        } else {
            //if there is no bluetooth adapter, save a null string with address
            pairedDevicesViewModel.initialized.observe(viewLifecycleOwner, {
                if(it == true) {
                    pairedDevicesViewModel.updateLastProfileWithBluetooth("")
                }
            })
            Log.e("DEBUGBLUETOOTH", "DONT HAVE BLUETOOTH ADAPTER")
        }

        // when viewModel confirm that profile has been saved,
        // change to next screen loading current profile fragment
        pairedDevicesViewModel.onUpdated.observe(viewLifecycleOwner, {
            if (it) {
                this.findNavController()
                    .navigate(R.id.action_pairedDevicesFragment_to_currentProfileFragment)
                pairedDevicesViewModel.doneChangeScreen()
            }
        })

        return binding.root
    }
}