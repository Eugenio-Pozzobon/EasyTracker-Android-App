package com.example.startracker.currentprofile

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.startracker.MainActivity
import com.example.startracker.R
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentCurrentProfileBinding
import kotlin.properties.Delegates


class CurrentProfileFragment : Fragment() {

    lateinit var binding:FragmentCurrentProfileBinding
    lateinit var currentProfileViewModel: CurrentProfileViewModel

    var btAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val REQUEST_ENABLE_BT=1

    var redButtonColor by Delegates.notNull<Int>()
    var greenButtonColor by Delegates.notNull<Int>()
    var whiteTextColor by Delegates.notNull<Int>()
    var yellowButtonColor by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // make the data binding for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_profile, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao

        val viewModelFactory = CurrentProfileViewModelFactory(dataSource, application)

        currentProfileViewModel = ViewModelProvider(this, viewModelFactory).get(
            CurrentProfileViewModel::class.java
        )

        binding.currentProfileViewModel = currentProfileViewModel

        binding.lifecycleOwner = this

        redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)
        yellowButtonColor = ContextCompat.getColor(requireContext(), R.color.yellow_button)

        //handle if user want to start alignment process
        currentProfileViewModel.screenChange.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                currentProfileViewModel.doneOnChangeScreen()
                this.findNavController()
                    .navigate(R.id.action_currentProfileFragment_to_levelAlignmentFragment)
            }
        })

        //rename the view fragment as the name of the selected profile
        currentProfileViewModel.profileName.observe(viewLifecycleOwner, {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = it
        })

        //check if exists an last seleted profile, if not, put the user and load profile screen
        currentProfileViewModel.noLastProfileAvailable.observe(viewLifecycleOwner, {
            if (it == true) {
                currentProfileViewModel.doneOnChangeScreen()
                this.findNavController()
                    .navigate(R.id.action_currentProfileFragment_to_loadProfilesFragment)
            }
        })

        currentProfileViewModel.bluetoothMac.observe(viewLifecycleOwner, {
            if (it != "") {
                if(startBluetooth()) {
                    connectWithBluetoothDevice()
                }
            }
        })

        //check if is a new user, so then put at the welcome screen
        currentProfileViewModel.newUserDetected.observe(viewLifecycleOwner, {
            if (it == true) {
                currentProfileViewModel.doneOnChangeScreen()
                this.findNavController()
                    .navigate(R.id.action_currentProfileFragment_to_welcomeFragment)
            }
        })

        binding.buttonConnect.setOnClickListener(){
            if(startBluetooth()) {
                connectWithBluetoothDevice()
            }
        }

        //change buttons and text colors
        binding.buttonConnect.setBackgroundColor(redButtonColor)
        binding.buttonConnect.setTextColor(whiteTextColor)

        binding.buttonStartAlignment.setBackgroundColor(redButtonColor)
        binding.buttonStartAlignment.setTextColor(whiteTextColor)
        binding.buttonConnect.text = getString(R.string.connect_status_init)

        // linked bluetooth with view model

        setHasOptionsMenu(true)
        return binding.root

    }


    private fun startBluetooth(): Boolean {
        var btOperationState = false
        if(btAdapter.isEnabled){
            btOperationState = true
        }else {
            //turn on bluetooth
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        return btOperationState
    }


    private fun connectWithBluetoothDevice() {
        binding.buttonConnect.text = getString(R.string.connecting_status)
        binding.buttonConnect.setBackgroundColor(yellowButtonColor)
        (activity as MainActivity).hc05.connect(currentProfileViewModel.bluetoothMac.value.toString())
        (activity as MainActivity).hc05.mmIsConnected.observeForever(checkConnection)
    }

    private val checkConnection = Observer<Boolean?>{
        if (it == true) {
            print(true)
            println(true)
            connectedWithBluetoothDevice()
        }else if(it == false){
            println(false)
            println(false)
            Toast.makeText(context, "Unnable to connect with device", Toast.LENGTH_SHORT).show()
            NotConnectedWithBluetoothDevice()
        }
    }

    private fun connectedWithBluetoothDevice() {
        currentProfileViewModel.onConnect()
        binding.buttonConnect.setBackgroundColor(greenButtonColor)
        binding.buttonStartAlignment.setBackgroundColor(greenButtonColor)
        binding.buttonConnect.text = getString(R.string.connect_status_sucessfull)
    }

    private fun NotConnectedWithBluetoothDevice() {
        currentProfileViewModel.notConnect()
        binding.buttonStartAlignment.setBackgroundColor(redButtonColor)
        binding.buttonConnect.setBackgroundColor(redButtonColor)
        binding.buttonConnect.setTextColor(whiteTextColor)
        binding.buttonConnect.text = getString(R.string.connect_status_init)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    connectWithBluetoothDevice()
                }else{
                    NotConnectedWithBluetoothDevice()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //inflate the overflow menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    //handle the user selection at overflow menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.newProfileFragment){
            (activity as MainActivity).hc05.disconnect()
        }

        if(item.itemId == R.id.loadProfilesFragment){
            (activity as MainActivity).hc05.disconnect()
        }

        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
                super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).hc05.mmIsConnected.value == true) {
            connectedWithBluetoothDevice()
        }else {
            NotConnectedWithBluetoothDevice()
        }
    }

}