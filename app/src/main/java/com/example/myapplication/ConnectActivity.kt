package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityConnectBinding
import java.io.IOException
import java.util.UUID

@Suppress("DEPRECATION")
class ConnectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConnectBinding

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var hc05Device: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Example UUID

    private val TAG = "ConnectActivity"

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        const val REQUEST_DISCOVERABLE = 2
        const val PERMISSION_REQUEST_CODE = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth is not supported on this device")
            return
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN),
                PERMISSION_REQUEST_CODE
            )
        }
        val discoveryIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300) // Set the discoverable duration in seconds
        startActivityForResult(discoveryIntent, REQUEST_DISCOVERABLE)

        hc05Device = bluetoothAdapter.getRemoteDevice("58:56:00:00:7A:99")

        try {
            bluetoothSocket = hc05Device.createRfcommSocketToServiceRecord(MY_UUID)
            bluetoothSocket.connect()
            // You can now send and receive data through the socket
        } catch (e: IOException) {
            Log.e(TAG, "Error connecting to HC-05: ${e.message}")
        }

        setupButtons()

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            bluetoothSocket.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing socket: ${e.message}")
        }
    }

        private fun setupButtons() {
        binding.forward.setOnClickListener { sendCommand("L") }
        binding.back.setOnClickListener { sendCommand("R") }
        binding.left.setOnClickListener { sendCommand("F") }
        binding.right.setOnClickListener { sendCommand("B") }
    }

        private fun sendCommand(input: String) {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}
