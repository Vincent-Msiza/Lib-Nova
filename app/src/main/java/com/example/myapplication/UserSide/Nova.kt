package com.example.myapplication.UserSide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ChatGptViewModel
import com.example.myapplication.MessageAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivityNovaBinding
import com.example.myapplication.model.Message
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class Nova : AppCompatActivity() {

    private val client = OkHttpClient()

    private lateinit var binding: ActivityNovaBinding
    private lateinit var chatGptViewModel: ChatGptViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatGptViewModel = ViewModelProvider(this)[ChatGptViewModel::class.java]

        val llm = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = llm

        chatGptViewModel.messageList.observe(this){messages ->
            val adapter = MessageAdapter(messages)
            binding.recyclerView.adapter = adapter
        }

        binding.sendBtn.setOnClickListener {
            val question = binding.messageEditText.text.toString()
            chatGptViewModel.addToChat(question, Message.SENT_BY_ME,chatGptViewModel.getCurrentTimestamp())
            binding.messageEditText.setText("")
            chatGptViewModel.callApi(question)
        }

    }

}