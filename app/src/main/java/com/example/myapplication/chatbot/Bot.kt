package com.example.myapplication.chatbot

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.API_KEY
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityBotBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import java.util.Objects

@Suppress("DEPRECATION")
class Bot : AppCompatActivity() {
    private val messageList = mutableListOf<Message>()
    private lateinit var messageAdapter: MessageAdapter
    private val client = OkHttpClient()
    private lateinit var binding: ActivityBotBinding
    private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

    // on below line we are creating a constant value
    private val REQUEST_CODE_SPEECH_INPUT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup recycler view
        messageAdapter = MessageAdapter(messageList)
        binding.recyclerView.adapter = messageAdapter
        val llm = LinearLayoutManager(this)
        llm.stackFromEnd = true
        binding.recyclerView.layoutManager = llm

        binding.voiceInputToggleBtn.setOnClickListener {
            // on below line we are calling speech recognizer intent.
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            // on below line we are passing language model
            // and model free form in our intent
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
                //back btn
            binding.backBtn.setOnClickListener {
                onBackPressed()
            }
            // on below line we are passing our
            // language as a default language.
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )

            // on below line we are specifying a prompt
            // message as speak to text on below line.
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            // on below line we are specifying a try catch block.
            // in this block we are calling a start activity
            // for result method and passing our result code.
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                // on below line we are displaying error message in toast
                Toast
                    .makeText(
                        this@Bot, " " + e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }}


        binding.sendBtn.setOnClickListener {
            val question = binding.messageEditText.text.toString().trim()
            addToChat(question, Message.SENT_BY_ME)
            binding.messageEditText.text.clear()
            callAPI(question)
            binding.welcomeText.visibility = View.GONE
        }
    }

    private fun addToChat(message: String, sentBy: String) {
        runOnUiThread {
            messageList.add(Message(message, sentBy))
            messageAdapter.notifyDataSetChanged()
            binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun addResponse(response: String) {
        messageList.removeAt(messageList.size - 1)
        addToChat(response, Message.SENT_BY_BOT)
    }

    private fun callAPI(question: String) {
        // okhttp
        messageList.add(Message("Typing... ", Message.SENT_BY_BOT))

        val jsonBody = JSONObject()
        try {
            jsonBody.put("model", "text-davinci-003")
            jsonBody.put("prompt", question)
            jsonBody.put("max_tokens", 4000)
            jsonBody.put("temperature", 0)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = RequestBody.create(JSON, jsonBody.toString())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization", "Bearer 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")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val errorMessage = "Failed to load response due to ${e.message}"
                addResponse(errorMessage)
                Log.e(TAG, errorMessage, e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val responseBody = response.body?.string() // Get the response body as a string
                        Log.d(TAG, "Response body: $responseBody")
                        val jsonObject = JSONObject(responseBody)
                        val jsonArray = jsonObject.getJSONArray("choices")
                        val result = jsonArray.getJSONObject(0).getString("text")
                        addResponse(result.trim())
                    } catch (e: JSONException) {
                        val errorMessage = "JSON parsing error: ${e.message}"
                        addResponse(errorMessage)
                        Log.e(TAG, errorMessage, e)
                    }
                } else {
                    val errorMessage = "Failed to load response due to ${response.code}"
                    addResponse(errorMessage)
                    Log.e(TAG, errorMessage)
                }
            }

        })
    }

    // on below line we are calling on activity result method.
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // in this method we are checking request
//        // code with our result code.
//        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
//            // on below line we are checking if result code is ok
//            if (resultCode == RESULT_OK && data != null) {
//
//                // in that case we are extracting the
//                // data from our array list
//                val res: ArrayList<String> =
//                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
//
//                // on below line we are setting data
//                // to our output text view.
//                binding.messageEditText.setText(
//                    Objects.requireNonNull(res)[0]
//                )
//            }
//        }
//}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

                val voiceInput = res[0]
                addToChat(voiceInput, Message.SENT_BY_ME)
                callAPI(voiceInput)
            }
        }
    }

}
