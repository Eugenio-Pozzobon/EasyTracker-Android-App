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

class BluetoothService {

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    val MESSAGE_READ: Int = 0

    private var _mmIsConnected = MutableLiveData<Boolean?>()
    val mmIsConnected: LiveData<Boolean?>
        get() = _mmIsConnected

    // raw data get in bluetooth connection
    private var _rawDataRoll: Int = 0
    private var _rawDataPitch: Int = 0
    private var _rawDataYaw: Int = 0
    private var _rawDataCRC: Int = 0
    private var _rawDataError: Boolean = false

    // data converted from bluetooth = raw/10
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

    private var _updatedHandle = MutableLiveData<Boolean>()
    val updatedHandle: LiveData<Boolean>
        get() = _updatedHandle

    // buffer read in bluetooth
    var stringBuffer: String = "0,0,0,0"

    // bluetooth address of device
    private var mmDeviceMAC: String = ""

    // thread that read bluetooth buffer data
    lateinit var RunnableThread: ConnectedThread

    init {
        _mmIsConnected.value = null
        _updatedHandle.value = false
    }

    // The Handler that gets information back from the BluetoothService
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val bundle: Bundle = msg.data

            // get buffer String
            stringBuffer = bundle.getString("key1", stringBuffer)
            val dataString = stringBuffer.split(",").toTypedArray()

            // check if buffer array have only 4 values
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
                // launch another thread for update UI values IF this values match CRC
                // CRC is just a sum with the other values
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
     * Connect with a bluetooth device specified by your address
     * Launch this in a separated thread
     * @param DeviceMAC that is the bluetooth address
     */
    fun connect(DeviceMAC: String) {
        thread {
            try {
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
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Disconnect with the current bluetooth device
     * Launch this in a separated thread
     */
    fun disconnect() {
        thread{
            try {
                if (_mmIsConnected.value == true) {
                    RunnableThread.disconnectThread()
                    _mmIsConnected.postValue(RunnableThread.mmThreadIsConnected)
                }
                _mmIsConnected.postValue(RunnableThread.mmThreadIsConnected)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Reconnect with a bluetooth device specified by your address
     * Launch this in a separated thread
     */
    fun reconnect() {
        thread{
            try {
                if (_mmIsConnected.value == true) { //disconect just if it is connected
                    RunnableThread.disconnectThread()
                }
                RunnableThread = ConnectedThread(mmDeviceMAC, handler)
                RunnableThread.connectThread()
                _mmIsConnected.postValue(RunnableThread.mmThreadIsConnected)
                if (RunnableThread.mmThreadIsConnected) {
                    RunnableThread.run()
                } else {
                    _mmIsConnected.postValue(false)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    //class that stands for run a thread for read and write in bluetooth device
    inner class ConnectedThread(var DeviceMAC: String, var handler: Handler) : Thread() {

        // bluetooth variables
        private lateinit var mmDevice: BluetoothDevice
        private lateinit var mmAdapter: BluetoothAdapter
        private lateinit var mmInStream: InputStream
        private lateinit var mmOutStream: OutputStream
        lateinit var mmSocket: BluetoothSocket

        //state of connection
        var mmThreadIsConnected = false
        var mmThreadIsDesconnecting = false

        // Unique UUID for this application
        // https://stackoverflow.com/questions/32130529/
        val myUUID: UUID? = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        // runs the thread for communicate with HC05
        override fun run() {

            // check if it still connected
            if (mmThreadIsConnected == true) {

                var getWriteTime: Long = System.currentTimeMillis()
                val buffer = ByteArray(1)
                var bytes: Int
                var readMessage = ""
                var readChar: String

                var getTime: Long = System.currentTimeMillis()
                while (true) {
                    // Keep listening to the InputStream until an exception occurs.
                    try {
                        //ensure that the buffer is allways clean
                        //delaytime is the delay that ui wait for read next buffer.
                        var delaytime = 10
                        if ((mmInStream.available() > 300)) {
                            // if the buffer is overload, i.e, is bigger then 230 bytes,
                            // reduce the delay so the UI can update faster enough
                            delaytime = 1
                        }
                        //if available and also get a delay between reeds
                        if ((mmInStream.available() > 0) &&
                            ((System.currentTimeMillis() - getTime) > delaytime)
                        ) {

                            bytes = mmInStream.read(buffer) //read bytes from input buffer
                            readChar = String(buffer, 0, bytes) //get Char

                            //if char isnt '\n' then join all char recieved,
                            // mounting the data string. Stop when '\n' and send it through handler
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
                        // in case of error, start disconnect if it not started
                        if (!mmThreadIsDesconnecting) {
                            mmThreadIsDesconnecting = true
                            disconnect()
                        }
                        break
                    }

                    // launch another thread to write in output buffer
                    thread {
                        // send messages with 2Hz of speed
                        if ((System.currentTimeMillis() - getWriteTime) > 500) {
                            getWriteTime = System.currentTimeMillis()
                            write("0".encodeToByteArray())
                        }
                    }

                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e("DEBUGCONNECTION", "Error occurred when sending data")

                if (!mmThreadIsDesconnecting) {
                    mmThreadIsDesconnecting = true
                    disconnect()
                }

                return
            } catch (e: Exception) {
                Log.e("DEBUGCONNECTION", "Error occurred when sending data")
            }
        }

        // This function check if the phone has bt adapter
        // and then connect with device using the UUID
        fun connectThread() {
            if (BluetoothAdapter.getDefaultAdapter() != null) {

                mmAdapter = BluetoothAdapter.getDefaultAdapter()
                mmThreadIsDesconnecting = false

                if (!mmAdapter.isEnabled) {
                    throw Exception("Bluetooth adapter not found or not enabled!")
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
            } else {
                Log.e("DEBUGBLUETOOTH", "DONT HAVE BLUETOOTH ADAPTER")
            }
        }

        // disconnect stream, socket and bluetooth device
        fun disconnectThread() {
            try { // close input
                mmInStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try { // close output
                mmOutStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try { // close socket
                mmSocket.close()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("DEBUGCONNECTION", "Could not close the connect socket")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DEBUGCONNECTION", "Could not close the connect socket")
            }
            // update state
            mmThreadIsConnected = mmSocket.isConnected
        }
    }
}


