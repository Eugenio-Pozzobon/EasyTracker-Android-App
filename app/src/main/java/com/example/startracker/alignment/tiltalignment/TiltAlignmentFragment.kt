package com.example.startracker.alignment.tiltalignment

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
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
import com.example.startracker.convertDpToPixel
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentTiltAlignmentBinding
import com.example.startracker.mapFloat
import com.google.android.material.snackbar.Snackbar

class TiltAlignmentFragment : Fragment() {

    private var redButtonColor: Int = 0
    private var greenButtonColor: Int = 0
    private var whiteTextColor: Int = 0

    private var circleMarginX: Float = 0F
    private var circleMarginY: Float = 0F
    private var mapFinalAngle: Float = 0F

    lateinit var btSnack:Snackbar
    lateinit var binding: FragmentTiltAlignmentBinding
    lateinit var tiltAlignmentViewModel: TiltAlignmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_tilt_alignment, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao
        val viewModelFactory = TiltAlignmentViewModelFactory(dataSource, application)

        tiltAlignmentViewModel = ViewModelProvider(this, viewModelFactory).get(
            TiltAlignmentViewModel::class.java
        )
        binding.tiltAlignmentViewModel = tiltAlignmentViewModel
        binding.lifecycleOwner = this

        redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.okButton.setBackgroundColor(redButtonColor)
        binding.okButton.setTextColor(whiteTextColor)

        //navigate for next stage of alignment
        binding.okButton.setOnClickListener(){
            this.findNavController().navigate(R.id.action_tiltAlignmentFragment_to_endAligmentFragment)
        }

        //when gps data is load at viewmodel, get it and show it as the target for indicate
        tiltAlignmentViewModel.gpsData.observe(viewLifecycleOwner, {
            //positioning of yellow marker
            try {
                addDestinationAngle(it.toFloat())
            }catch (e: Exception){
                Log.e("DEBUGAPP", "ERROR PARSING FLOAT", e)
            }
        })

        //activate observers in bluetooth data, so now its possible to update UI
        // with bluetooth values and tell user when it get disconnected
        (activity as MainActivity).hc05.updatedHandle.observeForever(handlerUpdateObserver)
        (activity as MainActivity).hc05.mmIsConnected.observeForever(checkConnection)

        setHasOptionsMenu(true)
        return binding.root
    }

    // Create an observer for check connection with bluetooth state variable in bluetooth service.
    // If it get false, create an Snackbar AND dialog that would warning user that state.
    lateinit var dialogBluetooth: AlertDialog
    private val checkConnection = Observer<Boolean?> {
        try {
            if (it != true) {
                try {

                    //indicate if was an disconnection fail or if just cant get connected
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
                    dialogBluetooth.show()

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
        if(startBluetooth()) {
            (activity as MainActivity).hc05.reconnect(tiltAlignmentViewModel.bluetoothMac.value.toString())
        }
    }

    // handler when hc05 variables get updated, transform it data in X,Y
    // variables for ploting marker in screen, convert it to DP and make an translation
    private val handlerUpdateObserver = Observer<Boolean> {
        try {
            updateAlignment()
            binding.circleAlignment.translationY =
                convertDpToPixel(circleMarginY, requireContext())
            binding.circleAlignment.translationX =
                convertDpToPixel(circleMarginX, requireContext())

        } catch (e: Exception) {
            Log.e("DEBUGCONNECTION", "Observer in Level Alignment not killed", e)
        }
    }

    // use an provide angle for change the yellow destination marker in screen
    private fun addDestinationAngle(finalAngle:Float){
        try {
            val valueMax = 90F
            val valueMin: Float = -90F

            val paddingMax = 115.0F
            val paddingMin: Float = -115.0F

            mapFinalAngle = mapFloat(finalAngle, valueMin, valueMax, paddingMin, paddingMax)

            if (mapFinalAngle < 0){
                mapFinalAngle = - mapFinalAngle
            }

            binding.destinationCircle.translationY =
                convertDpToPixel(mapFinalAngle, requireContext())

        } catch (e: Exception) {
        }
    }

    // Get values from HC05 and convert it for maximun dp of screen.
    // Calls an conversion for it
    private fun updateAlignment() {
        try {
            val pitch: Float? = (activity as MainActivity).hc05.dataPitch.value
            val roll: Float? = (activity as MainActivity).hc05.dataRoll.value

            val valueMax = 90F
            val valueMin: Float = -90F

            val paddingMax = 115.0F
            val paddingMin: Float = -115.0F

            if (((pitch!! <= mapFinalAngle + 0.2) && (pitch >= mapFinalAngle - 0.2)) && ((roll!! <=  0.2) && (roll >= -0.2))) {
                binding.okButton.setBackgroundColor(greenButtonColor)
            } else {
                binding.okButton.setBackgroundColor(redButtonColor)
            }

            circleMarginX = mapFloat(-pitch, valueMin, valueMax, paddingMin, paddingMax)
            circleMarginY = mapFloat(roll!!, valueMin, valueMax, paddingMin, paddingMax)
        } catch (e: Exception) {
            circleMarginX = 0F
            circleMarginY = 0F
        }
    }

    // inflate overflow menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu_alignment, menu)
    }

    //handler options of overflow menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.reconnect) {
            (activity as MainActivity).hc05.reconnect(tiltAlignmentViewModel.bluetoothMac.value.toString())
            return true
        }

        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
                super.onOptionsItemSelected(item)
    }

    //Check Fragment Lifecycle info https://abhiandroid.com/ui/fragment-lifecycle-example-android-studio.html
    override fun onPause() {
        super.onPause()
        (activity as MainActivity).hc05.updatedHandle.removeObserver(handlerUpdateObserver)
        (activity as MainActivity).hc05.mmIsConnected.removeObserver(checkConnection)
    }
}