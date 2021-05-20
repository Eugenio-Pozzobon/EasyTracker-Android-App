package com.example.startracker.alignment.polaralignment

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
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
import com.example.startracker.databinding.FragmentPolarAlignmentBinding
import com.google.android.material.snackbar.Snackbar


class PolarAlignmentFragment : Fragment() {

    private var redButtonColor: Int = 0
    private var greenButtonColor: Int = 0
    private var whiteTextColor: Int = 0

    private var rotate: Float = 0F
    private var declination: Float = 0F

    lateinit var btSnack: Snackbar
    lateinit var binding: FragmentPolarAlignmentBinding
    lateinit var polarAlignmentViewModel: PolarAlignmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

        binding.okButton.setOnClickListener() {
            this.findNavController()
                .navigate(R.id.action_polarAlignmentFragment_to_tiltAlignmentFragment)
        }

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
        if (startBluetooth()) {
            (activity as MainActivity).hc05.reconnect(polarAlignmentViewModel.bluetoothMac.value.toString())
        }
    }

    // handler when hc05 variables get updated, transform it data in X,Y
    // variables for ploting marker in screen, convert it to DP and make an translation
    private val handlerUpdateObserver = Observer<Boolean> {
        try {
            updateAlignment()
            binding.compass.rotation = rotate
        } catch (e: Exception) {
            Log.e("DEBUGCONNECTION", "Observer in Level Alignment not killed", e)
        }
    }

    // Get values from HC05 and convert it for maximun dp of screen.
    // Calls an conversion for it
    private fun updateAlignment() {
        try {
            declination = polarAlignmentViewModel.declination.value!!.toFloat()
            val yaw: Float? = (activity as MainActivity).hc05.dataYaw.value

            if (((yaw!! <= 0.2) && (yaw >= -0.2))) {
                binding.okButton.setBackgroundColor(greenButtonColor)
            } else {
                binding.okButton.setBackgroundColor(redButtonColor)
            }

            rotate = yaw - declination
        } catch (e: Exception) {
            rotate = 0F
        }
    }

    // inflate overflow menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu_compass, menu)
    }

    //handler options of overflow menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.reconnect) {
            reconnect()
            return true
        }

        if (item.itemId == R.id.calibrate_compass) {
            calibrateCompass()
            return true
        }

        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
                super.onOptionsItemSelected(item)
    }


    // CHECK CHECK CHECK CHECK
    private fun calibrateCompass() {

        val dialogStart: AlertDialog = AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.calibrate_compass_title))
            .setMessage(resources.getString(R.string.calibrate_compass_message))
            .setNegativeButton(resources.getString(R.string.decline_calibrate)) { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton(resources.getString(R.string.accept_calibrate)) { dialog, which ->

                val dialog: AlertDialog = AlertDialog.Builder(requireContext())
                    .setTitle(resources.getString(R.string.calibrate_compass_title))
                    .setMessage("Rotacione a plataforma horizontalmente pelos próximos 30 segundos")
                    .create()


                var valueCountdown = 30
                var correction = valueCountdown
                val AUTO_DISMISS_MILLIS = valueCountdown * 1000
                val countDown = object : CountDownTimer(AUTO_DISMISS_MILLIS.toLong(), 500) {
                    override fun onTick(millisUntilFinished: Long) {
                        valueCountdown =
                            (millisUntilFinished / 1000).toInt() + 1 - (AUTO_DISMISS_MILLIS / 1000 - correction)
                        dialog.setMessage(
                            "Rotacione a plataforma horizontalmente pelos próximos "
                                    + valueCountdown.toString() + " segundos"
                        )
                        if (!(valueCountdown > 0)) {
                            onFinish()
                        }
                    }

                    override fun onFinish() {
                        if (dialog.isShowing) {
                            dialog.dismiss()
                        }
                    }
                }
                dialog.setOnShowListener(object : OnShowListener {
                    override fun onShow(dialog: DialogInterface) {
                        countDown.start()
                    }
                })

                dialog.setOnDismissListener {
                    it.cancel()
                    if (valueCountdown > 1) {
                        countDown.cancel()
                        correction = valueCountdown
                        dialog.cancel()
                        dialog.show()
                    }
                }
                dialog.show()
            }
            .create()
        dialogStart.show()
    }

    //Check Fragment Lifecycle info https://abhiandroid.com/ui/fragment-lifecycle-example-android-studio.html
    override fun onPause() {
        super.onPause()
        (activity as MainActivity).hc05.updatedHandle.removeObserver(handlerUpdateObserver)
        (activity as MainActivity).hc05.mmIsConnected.removeObserver(checkConnection)
    }

}