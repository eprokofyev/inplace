package com.inplace.chat

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.inplace.api.ApiImageLoader
import com.inplace.api.vk.ApiVK
import com.inplace.models.Message
import com.inplace.models.Source
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ChatRepo {
    val LOG_TAG = "vkApi"

    private val executor: ExecutorService = Executors.newFixedThreadPool(5)

    private var messagesLiveData: MutableLiveData<List<Message>> = MutableLiveData<List<Message>>()
    private var avatarLiveData: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()

    init {
        messagesLiveData.value = emptyList()
    }


    fun getMessages(): MutableLiveData<List<Message>> {
        return messagesLiveData
    }

    fun fetchMessages(conversationId: Int, start: Int, end: Int) {
        executor.execute {
            val resultGetMessages = ApiVK.getMessages(conversationId, start, end)
            if (resultGetMessages.error != null) {
                //todo process error
            } else {
                val messagesArray = resultGetMessages.result as ArrayList<com.inplace.api.Message>
                messagesLiveData.postValue(transform(messagesArray))
            }
        }

    }

    fun getAvatar(): MutableLiveData<Bitmap> {
        return avatarLiveData
    }

    fun fetchAvatar(url: String, context: Context?) {
        executor.execute {
            val avatarBitmap = ApiImageLoader.getImageByUrl(url, context)
            avatarLiveData.postValue(avatarBitmap)
        }
    }

    fun sendMessage(conversationId: Int, message: String) {
        executor.execute {
            val result = ApiVK.sendMessage(conversationId, message)
            if (result.error != null) {
                Log.d(LOG_TAG, "Error while sending message")
            } else {
                Log.d(LOG_TAG, "Message successfully sent")
            }
        }
    }


    private fun transform(plains: List<com.inplace.api.Message>): List<Message> {
        val result: MutableList<Message> = ArrayList()
        plains.forEach {
            val message = map(it)
            result.add(message)
        }
        return result
    }

    private fun map(messagePlain: com.inplace.api.Message): Message {
        lateinit var messageSource: Source
        if (messagePlain.fromMessenger == 1)
            messageSource = Source.VK
        else if (messagePlain.fromMessenger == 2)
            messageSource = Source.TELEGRAM

        return Message(
                messagePlain.date * 1000L,
                messagePlain.text,
                messagePlain.fromId,
                messagePlain.myMsg,
                messageSource
        )
    }
}

