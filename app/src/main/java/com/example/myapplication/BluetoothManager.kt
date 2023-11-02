package com.example.myapplication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.AsyncTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.util.UUID

object BluetoothManager {
    var bluetoothSocket: BluetoothSocket? = null
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standard SerialPortService ID
    var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    fun isConnected(): Boolean {
        return bluetoothSocket?.isConnected == true
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        if (isConnected()) return
        AsyncTask.execute {
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID)
                bluetoothSocket?.connect()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun sendData(data: String) {
        if (!isConnected()) return
        try {
            bluetoothSocket?.outputStream?.write(data.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun closeConnection() {
        try {
            bluetoothSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        bluetoothSocket?.close()
    }

    // Adding a data listener interface
    interface DataListener {
        fun onDataReceived(data: String)
    }

    var dataListener: DataListener? = null

    var onDataReceivedListener: ((String) -> Unit)? = null

    fun startReading() {
        Thread {
            try {
                val buffer = ByteArray(1024)
                var bytes: Int
                while (true) {
                    try {
                        val inputStream = bluetoothSocket?.inputStream
                        if (inputStream != null) {
                            bytes = inputStream.read(buffer)
                            val readMessage = String(buffer, 0, bytes)
                            onDataReceivedListener?.invoke(readMessage)
                        }
                    } catch (e: IOException) {
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

}
