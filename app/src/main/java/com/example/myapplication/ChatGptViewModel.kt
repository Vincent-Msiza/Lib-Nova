package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.CompletionRequest
import com.example.myapplication.model.CompletionResponse
import com.example.myapplication.model.Message
import com.example.myapplication.data.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*

class ChatGptViewModel : ViewModel(){

    var url = "https://api.openai.com/v1/completions"
    private val _messageList = MutableLiveData<MutableList<Message>>()
    val messageList : LiveData<MutableList<Message>> get() = _messageList

    init {
        _messageList.value = mutableListOf()
    }

    fun addToChat(message : String , sentBy : String , timestamp : String){
        val currentList = _messageList.value ?: mutableListOf()
        currentList.add(Message(message,sentBy,timestamp))
        _messageList.postValue(currentList)
    }


    private fun addResponse(response : String){
        _messageList.value?.removeAt(_messageList.value?.size?.minus(1) ?: 0)
        addToChat(response,Message.SENT_BY_BOT,getCurrentTimestamp())
    }

    fun callApi(question : String){
        addToChat("Typing....",Message.SENT_BY_BOT,getCurrentTimestamp())

        val completionRequest = CompletionRequest(
            model = "text-davinci-003",
            prompt = question,
            max_tokens = 4000
        )

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getCompletions(completionRequest)
                handleApiResponse(response)
            }catch (e: SocketTimeoutException){
                addResponse("Timeout :  $e")
            }
        }
    }

    private suspend fun handleApiResponse(response: Response<CompletionResponse>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                val completionResponse = response.body()
                if (completionResponse != null) {
                    val result = completionResponse.choices.firstOrNull()?.text
                    if (result != null) {
                        addResponse(result.trim())
                    } else {
                        addResponse("No choices found")
                    }
                } else {
                    addResponse("Response body is null")
                }
            } else {
                addResponse("Failed to get response ${response.errorBody()}")
            }
        }
    }


    fun getCurrentTimestamp(): String {
        return SimpleDateFormat("hh mm a", Locale.getDefault()).format(Date())
    }


}