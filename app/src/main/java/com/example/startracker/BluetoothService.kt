package com.example.startracker

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.concurrent.thread

/**
 * A group of *members*.
 *
 * This class has no useful logic; it's just a documentation example.
 *
 * @param T the type of a member in this group.
 * @property name the name of this group.
 * @constructor Creates an empty group.
 */
class BluetoothService {

    // Defines several constants used when transmitting messages between the
// service and the UI.
    val MESSAGE_READ: Int = 0
    val MESSAGE_WRITE: Int = 1
    val MESSAGE_TOAST: Int = 2

    private var _mmIsConnected = MutableLiveData<Boolean?>()
    val mmIsConnected: LiveData<Boolean?>
        get() = _mmIsConnected

    private var _rawDataRoll: Int = 0
    private var _rawDataPitch: Int = 0
    private var _rawDataYaw: Int = 0
    private var _rawDataCRC: Int = 0
    private var _rawDataError: Boolean = false

    private var _dataRoll = MutableLiveData<Float>()
    val dataRoll: LiveData<Float>
        get() = _dataRoll

    private var _dataPitch = MutableLiveData<Float>()
    val dataPitch: LiveData<Float>
        get() = _dataPitch

    private var _dataYaw = MutableLiveData<Float>()
    val dataYaw: LiveData<Float>
        get() = _dataYaw

    private var _dataError1 = MutableLiveData<Boolean>()
    val dataError1: LiveData<Boolean>
        get() = _dataError1
// ... (Add other message types here as needed.)

    private var _updatedHandle = MutableLiveData<Boolean>()
    val updatedHandle: LiveData<Boolean>
        get() = _updatedHandle

    var stringBuffer: String = "0,0,0,0"

    private var mmDeviceMAC: String = ""

    lateinit var RunnableThread: ConnectedThread

    init {
        _mmIsConnected.value = null
        _updatedHandle.value = false
    }

    /**
     * The Handler that gets information back from the BluetoothService
     */
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val bundle: Bundle = msg.data;

            stringBuffer = bundle.getString("key1", stringBuffer)

            val dataString = stringBuffer.split(",").toTypedArray()
            if (dataString.size == 4) {
                try {
                    _rawDataRoll = dataString[0].toInt()
                    _rawDataPitch = dataString[1].toInt()
                    _rawDataYaw = dataString[2].toInt()
                    _rawDataCRC = dataString[3].toInt()
                    //_rawDataError:Boolean = dataString[0].toInt()
                } catch (e: Exception) {
                    Log.e("DEBUGCONNECTION", "Data Values with error", e)
                }
                thread {
                    if ((_rawDataRoll + _rawDataPitch + _rawDataYaw) == _rawDataCRC) {
                        _dataRoll.postValue(_rawDataRoll.toFloat() / 10)
                        _dataPitch.postValue(_rawDataPitch.toFloat() / 10)
                        _dataYaw.postValue(_rawDataYaw.toFloat() / 10)
                        _updatedHandle.postValue(!_updatedHandle.value!!)
                    }
                }
            }
        }
    }

    /**
     * Adds a [member] to this group.
     * @return the new size of the group.
     */
    fun connect(DeviceMAC: String) {
        thread {
            try {
                connecting(DeviceMAC)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun disconnect() {
        thread {
            try {
                disconnecting()
                _mmIsConnected.postValue(RunnableThread.mmThreadIsConnected)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun connecting(DeviceMAC: String) {
        if (_mmIsConnected.value != true) {
            mmDeviceMAC = DeviceMAC
            RunnableThread = ConnectedThread(DeviceMAC, handler)
            RunnableThread.connectThread()
            _mmIsConnected.postValue(RunnableThread.mmThreadIsConnected)
            if (RunnableThread.mmThreadIsConnected) {
                RunnableThread.run()
            } else {
                _mmIsConnected.postValue(false)
            }
        }
    }

    private fun disconnecting() {
        if (_mmIsConnected.value == true) {
            RunnableThread.disconnectThread()
            _mmIsConnected.postValue(RunnableThread.mmThreadIsConnected)
        }
    }

    inner class ConnectedThread(var DeviceMAC: String, var handler: Handler) : Thread() {

        private lateinit var mmDevice: BluetoothDevice
        private lateinit var mmAdapter: BluetoothAdapter

        lateinit var mmSocket: BluetoothSocket
        var mmThreadIsConnected = false
        var mmThreadIsDesconnecting = false

        // Unique UUID for this application
        val myUUID: UUID? = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        private lateinit var mmInStream: InputStream
        private lateinit var mmOutStream: OutputStream
        override fun run() {

            if (mmThreadIsConnected == true) {

                // Keep listening to the InputStream until an exception occurs.
                var getWriteTime: Long = System.currentTimeMillis()
                val buffer = ByteArray(1)
                var bytes: Int
                var readMessage: String = ""
                var readChar: String = ""

                var getTime: Long = System.currentTimeMillis()
                while (true) {
                    try {
                        if ((mmInStream.available() > 0) && ((System.currentTimeMillis() - getTime) > 15)) {
                            bytes = mmInStream.read(buffer) //read bytes from input buffer
                            readChar = String(buffer, 0, bytes)
                            if (readChar == "\n") {
                                getTime = System.currentTimeMillis()
                                val readMsg = handler.obtainMessage(MESSAGE_READ)
                                val bundle = Bundle()
                                bundle.putString("key1", readMessage)
                                readMsg.data = bundle
                                readMsg.sendToTarget()
                                readMessage = ""
                            } else {
                                readMessage += readChar
                            }
                        }
                    } catch (e: IOException) {
                        Log.e("DEBUGCONNECTION", "Input stream was disconnected")
                        if(!mmThreadIsDesconnecting) {
                            mmThreadIsDesconnecting = true
                            disconnect()
                        }
                        break
                    }

                    thread {
                        if ((System.currentTimeMillis() - getWriteTime) > 500) {
                            getWriteTime = System.currentTimeMillis()
                            write("0".encodeToByteArray())
                        }
                    }

                }
            }

            if(!mmThreadIsDesconnecting) {
                mmThreadIsDesconnecting = true
                disconnect()
            }
        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e("DEBUGCONNECTION", "Error occurred when sending data")

                // Send a failure message back to the activity.
//                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
//                val bundle = Bundle().apply {
//                    putString("toast", "Couldn't send data to the other device")
//                }
//                writeErrorMsg.data = bundle
//                handler.sendMessage(writeErrorMsg)

                if(!mmThreadIsDesconnecting) {
                    mmThreadIsDesconnecting = true
                    disconnect()
                }
                //connect(mmDeviceMAC)

                return
            } catch (e: Exception) {
                Log.e("DEBUGCONNECTION", "Error occurred when sending data")
            }

            // Share the sent message with the UI activity.
//            val writtenMsg = handler.obtainMessage(
//                MESSAGE_WRITE, -1, -1, mmBuffer
//            )
//            writtenMsg.sendToTarget()
        }

        fun connectThread() {
            mmAdapter = BluetoothAdapter.getDefaultAdapter()
            mmThreadIsDesconnecting = false

            if (!mmAdapter.isEnabled()) {
                throw Exception("Bluetooth adapter not found or not enabled!");
            }

            mmDevice = mmAdapter.getRemoteDevice(DeviceMAC)
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(myUUID)

            try {
                mmSocket.connect()
                mmThreadIsConnected = mmSocket.isConnected
                // Cancel discovery because it otherwise slows down the connection.
                mmAdapter.cancelDiscovery()
                mmInStream = mmSocket.inputStream
                mmOutStream = mmSocket.outputStream

            } catch (e: IOException) {
                mmThreadIsConnected = mmSocket.isConnected
                Log.e("DEBUGCONNECTION", "UNABLE TO CONNECT WITH BLUETOOTH DEVICE")
            }
        }


        fun disconnectThread() {
            try {
                mmInStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                mmOutStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                mmSocket.close()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("DEBUGCONNECTION", "Could not close the connect socket")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DEBUGCONNECTION", "Could not close the connect socket")
            }
            mmThreadIsConnected = mmSocket.isConnected
        }
    }
}


