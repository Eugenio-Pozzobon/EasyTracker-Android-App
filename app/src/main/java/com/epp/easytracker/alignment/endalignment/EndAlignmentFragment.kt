package com.epp.easytracker.alignment.endalignment

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.epp.easytracker.MainActivity
import com.epp.easytracker.R
import com.epp.easytracker.convertLongToDateString
import com.epp.easytracker.database.ProfileDatabase
import com.epp.easytracker.databinding.FragmentEndAlignmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlin.properties.Delegates

class EndAlignmentFragment : Fragment() {

    var redButtonColor by Delegates.notNull<Int>()
    var greenButtonColor by Delegates.notNull<Int>()
    var whiteTextColor by Delegates.notNull<Int>()

    lateinit var endAlignmentViewModel: EndAlignmentViewModel
    lateinit var binding: FragmentEndAlignmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
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

        redButtonColor = ContextCompat.getColor(requireContext(), R.color.bad_align_state)
        greenButtonColor = ContextCompat.getColor(requireContext(), R.color.align_state)
        whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)


        endAlignmentViewModel.updateTextTimer(getString(R.string.tracking_start_timer) +
                " 00:00:00\n" + getString(R.string.tracking_timer) + " 00:00:00")

        setButtonEnable(binding.startTrackingButton)
        binding.startTrackingButton.setOnClickListener() {
            if((activity as MainActivity).hc05.mmIsConnected.value == true) {
                setButtonDisable(binding.startTrackingButton)
                setButtonEnable(binding.endTrackingButton)
                binding.startTrackingButton.text = getString(R.string.tracking)
                (activity as MainActivity).hc05.updateWriteBuffer("s")
            }else{
                btSnack = Snackbar.make(
                    requireView(),
                    getString(R.string.reconnect_to_go),
                    Snackbar.LENGTH_SHORT,
                )
                btSnack.show()
            }
        }


        setButtonDisable(binding.endTrackingButton)
        binding.endTrackingButton.setOnClickListener() {
            setButtonEnable(binding.startTrackingButton)
            setButtonDisable(binding.endTrackingButton)
            binding.startTrackingButton.text = getString(R.string.start_track_button)
            (activity as MainActivity).hc05.updateWriteBuffer("n")
        }

        (activity as MainActivity).hc05.mmIsConnected.observeForever(checkConnection)

        dialogBluetooth = AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.blutooth_error_title))
            .setMessage(getString(R.string.fail_connection))
            .setNegativeButton(resources.getString(R.string.decline_calibrate)) { dialog, which ->
                dialog.dismiss()
            }.setPositiveButton(getString(R.string.bt_snack_action)) { dialog, which ->
                reconnect()
            }
            .create()

        setHasOptionsMenu(true)
//        (activity as MainActivity).setDrawer_locked()
//        (activity as MainActivity).toolbar.navigationIcon = null
        return binding.root
    }

    // Create an observer for check connection with bluetooth state variable in bluetooth service.
    // If it get false, create an Snackbar AND dialog that would warning user that state.
    private lateinit var dialogBluetooth: AlertDialog
    private lateinit var btSnack: Snackbar
    private val checkConnection = Observer<Boolean?> {
        try {
            if (it != true) {
                try {
                    //indicate if was an disconnection fail or if just cant get connected
                    dialogBluetooth.show()

                } catch (e: java.lang.Exception) {
                    Log.e("SNACKBARDEBUG", "SNACKBAR PROBLEM", e)
                }
            } else if (it == true) {
                try {
                    btSnack = Snackbar.make(
                        requireView(),
                        getString(R.string.done_connection),
                        Snackbar.LENGTH_SHORT,
                    )
                    btSnack.show()
                } catch (e: java.lang.Exception) {
                    Log.e("SNACKBARDEBUG", "SNACKBAR PROBLEM", e)
                }
            }
        } catch (e: Exception) {
            Log.e("DEBUGCONNECTION", "Observer in Level Alignment not killed", e)
        }

        if (isTracking) {
            countDown.cancel()
            endAlignmentViewModel.updateTextTimer(getString(R.string.tracking_bt_problem))
            setButtonEnable(binding.startTrackingButton)
            setButtonDisable(binding.endTrackingButton)
            binding.startTrackingButton.text = getString(R.string.start_track_button)
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
            btSnack = Snackbar.make(
                requireView(),
                getString(R.string.reconnecting),
                Snackbar.LENGTH_LONG,
            )
            btSnack.show()
            (activity as MainActivity).hc05.reconnect()
            if (!(activity as MainActivity).hc05.mmIsConnected.hasActiveObservers()) {
                (activity as MainActivity).hc05.mmIsConnected.observeForever(checkConnection)
            }
        }
        if (dialogBluetooth.isShowing) {
            dialogBluetooth.dismiss()
        }
    }

    var isTracking = false
    var taskTime: Long = 0L
    var taskInitTime: Long = 0L
    var stringTime = ""
    val AUTO_DISMISS_MILLIS = 9100000 //2h:30
    val countDown = object : CountDownTimer(AUTO_DISMISS_MILLIS.toLong(), 1000) {
        override fun onTick(millisUntilFinished: Long) {
            taskTime = System.currentTimeMillis() - taskInitTime + 3*3600*1000
            //display hours correctly
            stringTime = getString(R.string.tracking_start_timer) +
                    convertLongToDateString(taskInitTime,"' 'HH:mm")+
                    "\n" + getString(R.string.tracking_timer) +
                    convertLongToDateString(taskTime,"' 'HH:mm:ss")

            endAlignmentViewModel.updateTextTimer(stringTime)
            if (!(millisUntilFinished > 0)) {
                onFinish()
            }
        }

        override fun onFinish() {
        }
    }
    private val timerTrackingObserver = Observer<Boolean> {
        isTracking = it
        if (it) {
            taskInitTime = System.currentTimeMillis()
            countDown.start()
            setButtonDisable(binding.startTrackingButton)
            setButtonEnable(binding.endTrackingButton)
            binding.startTrackingButton.text = getString(R.string.tracking)
        } else {
            countDown.onFinish()
            countDown.cancel()
            setButtonEnable(binding.startTrackingButton)
            setButtonDisable(binding.endTrackingButton)
            binding.startTrackingButton.text = getString(R.string.start_track_button)
        }
    }

    private fun setButtonEnable(button: Button) {
        button.setBackgroundColor(greenButtonColor)
        button.setTextColor(whiteTextColor)
        button.isEnabled = true
    }

    private fun setButtonDisable(button: Button) {
        button.setBackgroundColor(redButtonColor)
        button.setTextColor(whiteTextColor)
        button.isEnabled = false
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
            this.findNavController()
                .navigate(R.id.action_endAligmentFragment_to_currentProfileFragment)
            return true
        }
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
                super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).hc05.trackingStars.removeObserver(timerTrackingObserver)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hc05.trackingStars.value = false
        (activity as MainActivity).hc05.trackingStars.observeForever(timerTrackingObserver)
        if (!(activity as MainActivity).hc05.mmIsConnected.hasActiveObservers()) {
            (activity as MainActivity).hc05.mmIsConnected.observeForever(checkConnection)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDown.onFinish()
        countDown.cancel()
    }
}