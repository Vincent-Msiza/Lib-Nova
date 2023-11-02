package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityControlBotBinding
import java.io.IOException
import java.util.UUID

class ControlBotActivity : AppCompatActivity() {
    //setup view binding
    private lateinit var binding: ActivityControlBotBinding
    //setup the bluetooth
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var hc05Device: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Example UUID
    private val TAG = "ControlBotActivity"
    companion object {
        const val REQUEST_ENABLE_BT = 1
        const val REQUEST_DISCOVERABLE = 2
        const val PERMISSION_REQUEST_CODE = 3
    }

    private var isRobotMoving = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlBotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setup bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        //connect to bluetooth btn
        //connect to bluetooth
//        binding.connect.setOnClickListener {
//            connect_bluetooth()
//        }
        //move back
        binding.backToHome.setOnClickListener {
            onBackPressed()
        }

        if (BluetoothManager.isConnected()) {

            binding.moveButton.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { // Button is pressed
                        forwardMovement()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { // Button is released or touch action is canceled
                        stopRobotMovement()
                    }
                }
                true
            }

            binding.backButton.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        backMovement()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        stopRobotMovement()
                    }
                }
                true
            }

            binding.rightButton.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        rightMovement()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        stopRobotMovement()
                    }
                }
                true
            }
            binding.leftButton.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        leftMovement()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        stopRobotMovement()
                    }
                }
                true
            }

//            binding.fetchButton.setOnTouchListener { _, event ->
//                when (event.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        fetchMovement()
//                    }
//                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                        stopRobotMovement()
//                    }
//                }
//                true
//            }
        }


        }
    private fun stopRobotMovement() {
        BluetoothManager.sendData("S")
    }
    private fun leftMovement() {
        BluetoothManager.sendData("L")
    }

    private fun rightMovement() {
        BluetoothManager.sendData("R")
    }
    private fun backMovement() {
        BluetoothManager.sendData("B")
    }
    private fun forwardMovement() {
        BluetoothManager.sendData("F")
    }

    private fun fetchMovement() {
        BluetoothManager.sendData("Y")
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::bluetoothSocket.isInitialized) {
                // Close the Bluetooth socket to release resources if it's initialized
                bluetoothSocket.close()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error closing Bluetooth socket: ${e.message}")
        }
    }

}
