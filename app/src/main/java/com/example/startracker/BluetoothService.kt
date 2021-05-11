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
import java.io.*
import java.util.*
import kotlin.concurrent.thread


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


    companion object {
        // Debugging
        private var mmDeviceMAC: String = ""

        // Unique UUID for this application
        val myUUID: UUID? = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        // Constants that indicate the current connection state
        val STATE_NONE = 0       // we're doing nothing
        val STATE_LISTEN = 1     // now listening for incoming connections
        val STATE_CONNECTING = 2 // now initiating an outgoing connection
        val STATE_CONNECTED = 3  // now connected to a remote device

        lateinit var ConnectThread: ConnectToDevice
        lateinit var RunnableThread: ConnectedThread

        lateinit var socket: BluetoothSocket
    }

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
                    _rawDataRoll = dataString[0]!!.toInt()
                    _rawDataPitch = dataString[1]!!.toInt()
                    _rawDataYaw = dataString[2]!!.toInt()
                    _rawDataCRC = dataString[3]!!.toInt()
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

    fun connect(DeviceMAC: String) {
        thread {
            try {
                if (_mmIsConnected.value != true) {
                    connecting(DeviceMAC)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun disconect() {
        thread {
            try {
                disconnecting()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun connecting(DeviceMAC: String) {
        mmDeviceMAC = DeviceMAC
        ConnectThread = ConnectToDevice(DeviceMAC)
        ConnectThread.run()
        //postValue - Posts a task to a main thread to set the given value.
        _mmIsConnected.postValue(ConnectThread.mmThreadIsConnected)
        socket = ConnectThread.mmSocket
        RunnableThread = ConnectedThread(socket, handler)
        RunnableThread.run()
    }

    private fun disconnecting() {
        if (_mmIsConnected.value == true) {
            BluetoothService.RunnableThread.disconnect()
            //postValue - Posts a task to a main thread to set the given value.
            _mmIsConnected.postValue(BluetoothService.ConnectThread.mmThreadIsConnected)
        }
    }


    fun reConnect(): Boolean? {
        disconnecting()
        _mmIsConnected.postValue(null)
        connecting(mmDeviceMAC)
        return _mmIsConnected.value
    }

    inner class ConnectToDevice(DeviceMAC: String) : Thread() {
        private lateinit var mmDevice: BluetoothDevice
        private lateinit var mmAdapter: BluetoothAdapter
        private var mmDeviceMAC: String = DeviceMAC

        lateinit var mmSocket: BluetoothSocket
        var mmThreadIsConnected = false

        override fun run() {

            super.run()
            mmAdapter = BluetoothAdapter.getDefaultAdapter()
            if (!mmAdapter.isEnabled()) {
                throw Exception("Bluetooth adapter not found or not enabled!");
            }

            mmDevice = mmAdapter.getRemoteDevice(mmDeviceMAC)
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(myUUID)
            try {
                mmSocket.connect()
                mmThreadIsConnected = mmSocket.isConnected
                // Cancel discovery because it otherwise slows down the connection.
                mmAdapter.cancelDiscovery()

            } catch (e: IOException) {
                mmThreadIsConnected = mmSocket.isConnected
            }
        }
    }

    inner class ConnectedThread(var mmSocket: BluetoothSocket, var handler: Handler) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private var reconnectCounter: Int = 0

        override fun run() {

            // Keep listening to the InputStream until an exception occurs.
            var getWriteTime: Long = System.currentTimeMillis()

            val buffer = ByteArray(1)
            var bytes: Int
            var readMessage: String = ""
            var readChar: String = ""

            var getTime: Long = System.currentTimeMillis()
            while (true) {
                try {
                    if ((mmInStream.available() > 0 ) && ((System.currentTimeMillis() - getTime) > 15)) {
                        bytes = mmInStream.read(buffer) //read bytes from input buffer
                        readChar = String(buffer, 0, bytes)
                        if (readChar == "\n") {
                            print(System.currentTimeMillis() - getTime)
                            print("    ")
                            println(mmInStream.available())
                            getTime = System.currentTimeMillis()
//                          Log.d("DEBUGCONNECTION", readMessage)
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
                    Log.e("DEBUGCONNECTION", "Input stream was disconnected", e)
                    break
                }

                thread {
                    if ((System.currentTimeMillis() - getWriteTime) > 750) {
                        getWriteTime = System.currentTimeMillis()
                        println("************************** sending: ")
                        write("0".encodeToByteArray())
                    }
                }

            }

            disconnect()
        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e("DEBUGCONNECTION", "Error occurred when sending data", e)

                // Send a failure message back to the activity.
//                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
//                val bundle = Bundle().apply {
//                    putString("toast", "Couldn't send data to the other device")
//                }
//                writeErrorMsg.data = bundle
//                handler.sendMessage(writeErrorMsg)
                //disconnect()
                if (reconnectCounter < 2) {
                    if (reConnect() == true) {
                        reconnectCounter = 0
                    } else {
                        reconnectCounter++
                    }
                }
                return
            } catch (e: Exception) {
                Log.e("DEBUGCONNECTION", "Error occurred when sending data", e)
            }

            // Share the sent message with the UI activity.
//            val writtenMsg = handler.obtainMessage(
//                MESSAGE_WRITE, -1, -1, mmBuffer
//            )
//            writtenMsg.sendToTarget()
        }


        fun disconnect() {
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
                Log.e("DEBUGCONNECTION", "Could not close the connect socket", e)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DEBUGCONNECTION", "Could not close the connect socket", e)
            }
        }
    }
}


