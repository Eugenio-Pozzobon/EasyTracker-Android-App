package com.epp.easytracker.alignment.levelalignment

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.epp.easytracker.*
import com.epp.easytracker.database.ProfileDatabase
import com.epp.easytracker.databinding.FragmentLevelAlignmentBinding
import com.google.android.material.snackbar.Snackbar

class LevelAlignmentFragment : Fragment() {

    private var redButtonColor: Int = 0
    private var greenButtonColor: Int = 0
    private var whiteTextColor: Int = 0

    private var circleMarginX: Float = 0F
    private var circleMarginY: Float = 0F

    private lateinit var binding: FragmentLevelAlignmentBinding
    private lateinit var levelAlignmentViewModel: LevelAlignmentViewModel
    lateinit var btSnack: Snackbar

    lateinit var leftArrow:ImageView
    lateinit var rightArrow:ImageView
    lateinit var upArrow:ImageView
    lateinit var downArrow:ImageView

    var isPositioned = false
    var lastState = false
    private val countDownArrow = object : CountDownTimer(3600 * 24 * 1000, 250) {
        override fun onTick(millisUntilFinished: Long) {
            try {
                lastState = !lastState
                if (!lastState && !isPositioned) {
                    if (pitch!! > errorMargin) {
                        upArrow.isVisible = true
                    } else if (pitch!! < -errorMargin) {
                        downArrow.isVisible = true
                    } else {
                        upArrow.isVisible = false
                        downArrow.isVisible = false
                    }

                    if (roll!! > errorMargin) {
                        leftArrow.isVisible = true
                    } else if (roll!! < -errorMargin) {
                        rightArrow.isVisible = true
                    } else {
                        leftArrow.isVisible = false
                        rightArrow.isVisible = false
                    }
                } else {
                    leftArrow.isVisible = false
                    rightArrow.isVisible = false
                    upArrow.isVisible = false
                    downArrow.isVisible = false
                }
            }catch (e: java.lang.Exception){
                Log.e("LEVELALIGN", "TICK PROBLEM LEVEL ALIGNMENT", e)
            }
        }

        override fun onFinish() {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_level_alignment, container, false
        )
        ivVectorImage = binding.circleAlignment
        leftArrow = binding.leftArrow
        rightArrow = binding.rightArrow
        upArrow = binding.topArrow
        downArrow = binding.bottomArrow

        val application = requireNotNull(this.activity).application
        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao
        val viewModelFactory = LevelAlignmentViewModelFactory(dataSource, application)

        levelAlignmentViewModel = ViewModelProvider(this, viewModelFactory).get(
            LevelAlignmentViewModel::class.java
        )
        binding.levelAlignmentViewModel = levelAlignmentViewModel
        binding.lifecycleOwner = this

        redButtonColor = ContextCompat.getColor(requireContext(), R.color.bad_align_state)
        greenButtonColor = ContextCompat.getColor(requireContext(), R.color.align_state)
        whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.okButton.setBackgroundColor(redButtonColor)
        binding.okButton.setTextColor(whiteTextColor)

        //navigate for next stage of alignment
        binding.okButton.setOnClickListener() {
            this.findNavController()
                .navigate(R.id.action_levelAlignmentFragment_to_polarAlignmentFragment)
        }

        //activate observers in bluetooth data, so now its possible to update UI
        // with bluetooth values and tell user when it get disconnected
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

        return binding.root
    }

    // Create an observer for check connection with bluetooth state variable in bluetooth service.
    // If it get false, create an Snackbar that would warning user that state.
    private lateinit var dialogBluetooth: AlertDialog
    private val checkConnection = Observer<Boolean?> {
        try {
            if (it != true) {
                try {

                    //indicate if was an disconnection fail or if just cant get connected
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
            btSnack = Snackbar.make(
                requireView(),
                getString(R.string.reconnecting),
                Snackbar.LENGTH_LONG,
            )
            btSnack.show()
            (activity as MainActivity).hc05.reconnect(requireContext())
            if(!(activity as MainActivity).hc05.mmIsConnected.hasActiveObservers()) {
                (activity as MainActivity).hc05.mmIsConnected.observeForever(checkConnection)
            }
        }
        if (dialogBluetooth.isShowing) {
            dialogBluetooth.dismiss()
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


    // Get values from HC05 and convert it for maximun dp of screen.
    // Calls an conversion for it
    lateinit var ivVectorImage: ImageView
    var pitch: Float? = 0F
    var roll: Float? = 0F
    private fun updateAlignment() {
        try {
            pitch = (activity as MainActivity).hc05.dataPitch.value
            roll = (activity as MainActivity).hc05.dataRoll.value

            val valueMax = 90F
            val valueMin: Float = -90F

            val paddingMax = 115.0F
            val paddingMin: Float = -115.0F

            if (((pitch!! <= errorMargin) && (pitch!! >= -errorMargin)) && ((roll!! <= errorMargin) && (roll!! >= -errorMargin))) {
                binding.okButton.setBackgroundColor(greenButtonColor)
                ivVectorImage.setColorFilter(greenButtonColor)
                isPositioned = true

            } else {
                binding.okButton.setBackgroundColor(redButtonColor)
                ivVectorImage.setColorFilter(redButtonColor)
                isPositioned = false
            }

            circleMarginX = mapFloat(roll!!, valueMin, valueMax, paddingMin, paddingMax)
            circleMarginY = mapFloat(pitch!!, valueMin, valueMax, paddingMin, paddingMax)
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

    // handler options of overflow menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.reconnect) {
            reconnect()
            return true
        }

        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
                super.onOptionsItemSelected(item)
    }

    // Check Fragment Lifecycle info https://abhiandroid.com/ui/fragment-lifecycle-epp-android-studio.html
    override fun onPause() {
        super.onPause()
        (activity as MainActivity).hc05.updatedHandle.removeObserver(handlerUpdateObserver)
        (activity as MainActivity).hc05.mmIsConnected.removeObserver(checkConnection)
        countDownArrow.onFinish()
        countDownArrow.cancel()
    }
    override fun onResume() {
        super.onResume()
        //activate observers in bluetooth data, so now its possible to update UI
        // with bluetooth values and tell user when it get disconnected
        (activity as MainActivity).hc05.updatedHandle.observeForever(handlerUpdateObserver)
        if(!(activity as MainActivity).hc05.mmIsConnected.hasActiveObservers()) {
            (activity as MainActivity).hc05.mmIsConnected.observeForever(checkConnection)
        }
        countDownArrow.start()
    }
}