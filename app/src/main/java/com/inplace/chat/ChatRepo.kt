package com.inplace.chat

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.inplace.api.ApiImageLoader
import com.inplace.api.vk.ApiVk


import com.inplace.models.Message
import com.inplace.models.Source
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ChatRepo {
    val LOG_TAG = "vkApi"

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var avatarLiveData: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()

    fun getAvatar() = avatarLiveData

    fun fetchAvatar(url: String, context: Context?) {
        executor.execute {
            val imageLoader  = ApiImageLoader.getInstance(context)
            val avatarBitmap = imageLoader.getImageByUrl(url)
            avatarLiveData.postValue(avatarBitmap)
        }
    }

//    fun sendMessage(conversationId: Int, message: String) {
//        executor.execute {
//            val result = ApiVK.sendMessage(conversationId, message)
//            if (result.error != null) {
//                Log.d(LOG_TAG, "Error while sending message")
//            } else {
//                Log.d(LOG_TAG, "Message successfully sent")
//            }
//        }
//    }

    fun getMessages(
        conversationId: Int,
        start: Int,
        end: Int,
        callback: OnChatRequestCompleteListener
    ) {
        executor.execute {
            val response = ApiVk.getMessages(conversationId, start, end)
            callback.onChatRequestComplete(
                if (response.error != null) {
                    ChatRepoResult.Error(Exception(response.errTextMsg))
                } else {
                    val messages = response.result as List<Message>
                    ChatRepoResult.Success(transform(messages))
                }
            )


        }
    }

    private fun transform(plains: List<Message>): List<Message> {
        val result: MutableList<Message> = ArrayList()
        plains.forEach {
            val message = map(it)
            result.add(message)
        }
        return result
    }

    private fun map(messagePlain: Message): Message {
        return Message(
            messageID = messagePlain.messageID,
            date = messagePlain.date * 1000L,
            text = messagePlain.text,
            userID = messagePlain.userID,
            chatID = messagePlain.chatID,
            myMsg = messagePlain.myMsg,
            fromMessenger = messagePlain.fromMessenger,
            isRead = messagePlain.isRead,
            photos = messagePlain.photos
        )
    }

    fun interface OnChatRequestCompleteListener {
        fun onChatRequestComplete(result: ChatRepoResult)
    }
}

