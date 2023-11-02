
package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.chatbot.Bot
import com.example.myapplication.databinding.ActivityControlBinding
import java.io.IOException

class ControlActivity : AppCompatActivity() {
    private lateinit var binding: ActivityControlBinding

    //setup the bluetooth
//    private lateinit var bluetoothAdapter: BluetoothAdapter
//    private lateinit var hc05Device: BluetoothDevice
//    private lateinit var bluetoothSocket: BluetoothSocket
//    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Example UUID
    private val TAG = "ControlBotActivity"
    companion object {
        const val REQUEST_ENABLE_BT = 1
        const val REQUEST_DISCOVERABLE = 2
        const val PERMISSION_REQUEST_CODE = 1001
    }

    private var isConnected = false  // Variable to track Bluetooth connection status

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.connectBT.setOnClickListener {
            // Check for Bluetooth permissions and initiate connection
            if (isConnected) {
                // Disconnect Bluetooth
                disconnectBluetoothDevice()
            }else {
                // Check for Bluetooth permissions and initiate connection
                if (hasBluetoothPermissions()) {
                    connectToBluetoothDevice()
                } else {
                    requestBluetoothPermissions()
                }
            }

        }

        //for our ai
        binding.ourAi.setOnClickListener {
            startActivity(Intent(this, Bot::class.java))
        }
        //voice command
        binding.voiceCommand.setOnClickListener {
            startActivity(Intent(this, VoiceCommandActivity::class.java))
        }
        //go to btn control
        binding.control.setOnClickListener {
            startActivity(Intent(this, ControlBotActivity::class.java))
        }
        binding.fetchBooks.setOnClickListener {
            startActivity(Intent(this, FetchBooksActivity::class.java))
        }

        BluetoothManager.onDataReceivedListener = { data ->
            onDataReceived(data)
        }

    }

    private fun hasBluetoothPermissions(): Boolean {
        val bluetoothPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
        val bluetoothAdminPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
        val locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        return bluetoothPermission == PackageManager.PERMISSION_GRANTED &&
                bluetoothAdminPermission == PackageManager.PERMISSION_GRANTED &&
                locationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        ), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            connectToBluetoothDevice()
        } else {
            Toast.makeText(this, "Permissions denied!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun connectToBluetoothDevice() {
        val deviceAddress = "58:56:00:00:7A:99"
        val device: BluetoothDevice? = BluetoothManager.bluetoothAdapter?.getRemoteDevice(deviceAddress)
        device?.let {
            BluetoothManager.connectToDevice(it)
            isConnected = true  // Update connection status
            updateConnectButton()  // Update button text
            BluetoothManager.startReading()
        }
    }

    private fun disconnectBluetoothDevice() {

        try {
            BluetoothManager.bluetoothSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        isConnected = false  // Update connection status
        updateConnectButton()  // Update button text
    }

     private fun updateConnectButton() {
        if (isConnected) {
            binding.connectBT.text = "Disconnect"
        } else {
            binding.connectBT.text = "Connect"
        }
    }

     @SuppressLint("SetTextI18n")
     fun onDataReceived(data: String) {
        runOnUiThread {
            // You can split the data and display it accordingly
            val values = data.split(',')
            if (values.size >= 2) {
                val inchesValue = values[0].trim()
                val cmValue = values[1].trim()
                binding.read.text = "$inchesValue, $cmValue"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the listener when activity is destroyed
//        if (BluetoothManager.dataListener == this) {
//            BluetoothManager.dataListener = null
//        }
    }


}