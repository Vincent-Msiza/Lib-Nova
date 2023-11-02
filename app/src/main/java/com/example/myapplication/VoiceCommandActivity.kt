package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityVoiceCommandBinding
import java.io.IOException
import java.util.Locale
import java.util.UUID

class VoiceCommandActivity : AppCompatActivity() {
    //binding
    private lateinit var binding: ActivityVoiceCommandBinding
    //set progress dialog
    private lateinit var progressDialog: ProgressDialog

    //declaration for connecting to bluetooth
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var hc05Device: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Example UUID
    private val TAG = "ControlActivity"
    companion object {
        const val REQUEST_ENABLE_BT = 1
        const val REQUEST_DISCOVERABLE = 2
        const val PERMISSION_REQUEST_CODE = 3
        private const val REQUEST_AUDIO_PERMISSION = 100
        private const val REQUEST_SPEECH_RECOGNITION = 200
    }
    //text to speech declare
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceCommandBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setup the bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        //set up progress dialog

        //move back
        binding.backToHome.setOnClickListener {
            onBackPressed()
        }
        // Initialize Text-to-Speech
        binding.commandBtn.setOnClickListener {

            textToSpeech = TextToSpeech(this) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val locale = Locale.getDefault()
                    if (textToSpeech.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
                        textToSpeech.language = locale
                    }
                } else {
                    Log.e("VoiceCommand", "TextToSpeech initialization failed")
                }
            }
            // Request permission to record audio for speech recognition
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_AUDIO_PERMISSION
                )
            }

            // Start speech recognition
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak a command")
            startActivityForResult(intent, REQUEST_SPEECH_RECOGNITION)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SPEECH_RECOGNITION && resultCode == Activity.RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (results != null && results.isNotEmpty()) {
                val spokenText = results[0]
                processVoiceCommand(spokenText)
            }
        }

    }
    //send a command function
    private fun sendCommand(input: String) {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    //speech to text
    private fun processVoiceCommand(command: String) {
        // Process voice command and control the robot
        when (command.toLowerCase()) {
            "forward" -> {
                // Execute the robot command to move forward
//                sendCommand("L")
                BluetoothManager.sendData("F")
                speak("Moving forward.")
            }
            "go" -> {
                // Execute the robot command to move forward
//                sendCommand("L")
                BluetoothManager.sendData("F")
                speak("Moving forward.")
            }
            "backward" -> {
                // Execute the robot command to move backward
//                sendCommand("R")
                BluetoothManager.sendData("B")
                speak("Moving backward.")
            }
            "reverse" -> {
                // Execute the robot command to move backward
//                sendCommand("R")
                BluetoothManager.sendData("B")
                speak("Moving backward.")
            }
            "left" -> {
                // Execute the robot command to move backward
//                sendCommand("F")
                BluetoothManager.sendData("L")
                speak("Moving left.")
            }
            "turn left" -> {
                // Execute the robot command to move backward
//                sendCommand("F")
                BluetoothManager.sendData("L")
                speak("Moving left.")
            }
            "right" -> {
                // Execute the robot command to move backward
//                sendCommand("B")
                BluetoothManager.sendData("R")
                speak("Moving right.")
            }
            "turn right" -> {
                // Execute the robot command to move backward
//                sendCommand("B")
                BluetoothManager.sendData("R")
                speak("Moving right.")
            }
            "fetch" -> {
                // Execute the robot command to move backward
//                sendCommand("Y")
                BluetoothManager.sendData("Y")
                speak("Fetching.")
            }
            "collect books" -> {
                // Execute the robot command to move backward
//                sendCommand("Y")
                BluetoothManager.sendData("Y")
                speak("Fetching.")
            }
            "stop" -> {
                // Execute the robot command to move backward
//                sendCommand("S")
                BluetoothManager.sendData("S")
                speak("Stopping.")
            }
            // Add more commands and corresponding responses here
            else -> {
                speak("Sorry, I didn't understand the command.")
            }
        }
    }
    private fun speak(text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
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