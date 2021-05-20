package com.example.startracker.currentprofile

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
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
import com.google.android.material.snackbar.Snackbar
import kotlin.properties.Delegates


class CurrentProfileFragment : Fragment() {

    var forceDisconnection = false
    lateinit var btSnack: Snackbar

    lateinit var binding: FragmentCurrentProfileBinding
    lateinit var currentProfileViewModel: CurrentProfileViewModel

    lateinit var btAdapter: BluetoothAdapter
    private val REQUEST_ENABLE_BT = 1

    var redButtonColor by Delegates.notNull<Int>()
    var greenButtonColor by Delegates.notNull<Int>()
    var whiteTextColor by Delegates.notNull<Int>()
    var yellowButtonColor by Delegates.notNull<Int>()

    lateinit var dialogBluetooth: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // make the data binding for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_current_profile, container, false)

        // crate and start view model and it variables.
        val application = requireNotNull(this.activity).application
        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao
        val viewModelFactory = CurrentProfileViewModelFactory(dataSource, application)

        currentProfileViewModel = ViewModelProvider(this, viewModelFactory).get(
            CurrentProfileViewModel::class.java
        )
        binding.currentProfileViewModel = currentProfileViewModel
        binding.lifecycleOwner = this

        //get Colors info
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

        //Connect bluetooth device when screen load
        currentProfileViewModel.bluetoothMac.observe(viewLifecycleOwner, {
            if (it != "") {
                if (startBluetooth()) {
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

        //connect with bluetooth device
        binding.buttonConnect.setOnClickListener() {
            if (startBluetooth()) {
                connectWithBluetoothDevice()
            }
//            currentProfileViewModel.onConnect()
//            binding.buttonConnect.setBackgroundColor(greenButtonColor)
//            binding.buttonStartAlignment.setBackgroundColor(greenButtonColor)
//            binding.buttonConnect.text = getString(R.string.connect_status_sucessfull)
        }

        //change buttons and text colors
        binding.buttonConnect.setBackgroundColor(redButtonColor)
        binding.buttonConnect.setTextColor(whiteTextColor)

        binding.buttonStartAlignment.setBackgroundColor(redButtonColor)
        binding.buttonStartAlignment.setTextColor(whiteTextColor)
        binding.buttonConnect.text = getString(R.string.connect_status_init)

        setHasOptionsMenu(true)
        return binding.root

    }

    // Check if the device has bluetooth and return if it is enable.
    // If not, call an Intent that is handle in onActivityResult()
    private fun startBluetooth(): Boolean {

        var btOperationState = false
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            btAdapter = BluetoothAdapter.getDefaultAdapter()
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


    private fun connectWithBluetoothDevice() {
        binding.buttonConnect.text = getString(R.string.connecting_status)
        binding.buttonConnect.setBackgroundColor(yellowButtonColor)
        (activity as MainActivity).hc05.connect(currentProfileViewModel.bluetoothMac.value.toString())
        (activity as MainActivity).hc05.mmIsConnected.observeForever(checkConnection)
    }

    // Create an observer for check connection with bluetooth state variable in bluetooth service.
    // If it get false, create an Snackbar that would warning user that state.
    private val checkConnection = Observer<Boolean?> {
        if (it == true) {
            _connectedWithBluetoothDevice()
        } else if (it == false) {
            _notConnectedWithBluetoothDevice()
            try {
                //indicate if was an disconnection fail or if just cant get connected
                if (forceDisconnection) {
                    //don't notify user
                } else {
                    dialogBluetooth = AlertDialog.Builder(requireContext())
                        .setTitle(resources.getString(R.string.blutooth_error_title))
                        .setMessage(getString(R.string.fail_connection))
                        .setNegativeButton(resources.getString(R.string.decline_calibrate)) { dialog, which ->
                            // Respond to negative button press
                        }.setPositiveButton(getString(R.string.bt_snack_action)) {dialog, which ->
                            if(startBluetooth()){
                                reconnect()
                            }
                        }
                        .create()
                }
                dialogBluetooth.show()
            } catch (e: Exception) {
                Log.e("SNACKBARDEBUG", "SNACKBAR PROBLEM", e)
            }
        }
    }

    // is bluetooth is connected, disconnect before connection, and make an reconnection.
    private fun reconnect() {
        if ((activity as MainActivity).hc05.mmIsConnected.value == true) {
            (activity as MainActivity).hc05.disconnect()
        }
        connectWithBluetoothDevice()
    }

    // Indicate to viewModel that hc05 is connected and this would set the "Start Alignment" button as visible.
    // Modify others buttons as it indicate the connection state.
    private fun _connectedWithBluetoothDevice() {
        currentProfileViewModel.onConnect()
        binding.buttonConnect.setBackgroundColor(greenButtonColor)
        binding.buttonStartAlignment.setBackgroundColor(greenButtonColor)
        binding.buttonConnect.text = getString(R.string.connect_status_sucessfull)
    }

    // Indicate to viewModel that hc05 is NOT connected and this would set the "Start Alignment" button as NOT visible.
    // Modify others buttons as it indicate the connection state
    private fun _notConnectedWithBluetoothDevice() {
        currentProfileViewModel.notConnect()
        binding.buttonStartAlignment.setBackgroundColor(redButtonColor)
        binding.buttonConnect.setBackgroundColor(redButtonColor)
        binding.buttonConnect.setTextColor(whiteTextColor)
        binding.buttonConnect.text = getString(R.string.connect_status_init)
    }

    // This function is android based for review an Activity intent.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    connectWithBluetoothDevice()
                } else {
                    _notConnectedWithBluetoothDevice()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // inflate the overflow menu.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu_currentprofile, menu)
    }

    //handle the user selection at the menus.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.newProfileFragment) {
            if ((activity as MainActivity).hc05.mmIsConnected.value == true) {
                (activity as MainActivity).hc05.disconnect()
                forceDisconnection = true
            }
        }

        if (item.itemId == R.id.loadProfilesFragment) {
            if ((activity as MainActivity).hc05.mmIsConnected.value == true) {
                (activity as MainActivity).hc05.disconnect()
                forceDisconnection = true
            }
        }

        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
                super.onOptionsItemSelected(item)
    }


    //Check Fragment Lifecycle info https://abhiandroid.com/ui/fragment-lifecycle-example-android-studio.html
    override fun onResume() {
        super.onResume()
        forceDisconnection = false
        if ((activity as MainActivity).hc05.mmIsConnected.value == true) {
            _connectedWithBluetoothDevice()
        } else {
            _notConnectedWithBluetoothDevice()
        }
    }

}