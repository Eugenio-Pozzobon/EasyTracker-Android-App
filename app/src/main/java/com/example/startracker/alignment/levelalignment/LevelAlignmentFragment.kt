package com.example.startracker.alignment.levelalignment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
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
import com.example.startracker.convertDpToPixel
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.databinding.FragmentLevelAlignmentBinding
import com.example.startracker.mapFloat
import com.google.android.material.snackbar.Snackbar

class LevelAlignmentFragment : Fragment() {

    private var circleMarginX: Float = 0F
    private var circleMarginY: Float = 0F
    private lateinit var binding: FragmentLevelAlignmentBinding
    private lateinit var levelAlignmentViewModel:LevelAlignmentViewModel

    private var redButtonColor: Int = 0
    private var greenButtonColor: Int = 0
    private var whiteTextColor: Int = 0

    lateinit var btSnack:Snackbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_level_alignment, container, false
        )

        val application = requireNotNull(this.activity).application

        val dataSource = ProfileDatabase.getInstance(application).profileDatabaseDao

        val viewModelFactory = LevelAlignmentViewModelFactory(dataSource, application)

        levelAlignmentViewModel = ViewModelProvider(this, viewModelFactory).get(
            LevelAlignmentViewModel::class.java
        )

        binding.levelAlignmentViewModel = levelAlignmentViewModel

        binding.lifecycleOwner = this

        redButtonColor = ContextCompat.getColor(requireContext(), R.color.red_button)
        greenButtonColor = ContextCompat.getColor(requireContext(), R.color.green_button)
        whiteTextColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.okButton.setBackgroundColor(redButtonColor)
        binding.okButton.setTextColor(whiteTextColor)

        binding.okButton.setOnClickListener() {
            this.findNavController()
                .navigate(R.id.action_levelAlignmentFragment_to_polarAlignmentFragment)
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
                        (activity as MainActivity).hc05.reconnect(levelAlignmentViewModel.bluetoothMac.value.toString())
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
            binding.circleAlignment.translationY =
                convertDpToPixel(circleMarginY, requireContext())
            binding.circleAlignment.translationX =
                convertDpToPixel(circleMarginX, requireContext())
        } catch (e: Exception) {
            Log.e("DEBUGCONNECTION", "Observer in Level Alignment not killed", e)
        }
    }

    private fun updateAlignment() {
        try {
            val pitch: Float? = (activity as MainActivity).hc05.dataPitch.value
            val roll: Float? = (activity as MainActivity).hc05.dataRoll.value

            val valueMax: Float = 90F
            val valueMin: Float = -90F

            val paddingMax: Float = 115.0F
            val paddingMin: Float = -115.0F

            if (((pitch!! <= 0.2) && (pitch >= -0.2)) && ((roll!! <= 0.2) && (roll >= -0.2))) {
                binding.okButton.setBackgroundColor(greenButtonColor)
            } else {
                binding.okButton.setBackgroundColor(redButtonColor)
            }

            circleMarginX = mapFloat(-pitch!!, valueMin, valueMax, paddingMin, paddingMax)
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
            (activity as MainActivity).hc05.reconnect(levelAlignmentViewModel.bluetoothMac.value.toString())
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