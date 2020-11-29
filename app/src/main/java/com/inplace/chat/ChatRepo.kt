package com.inplace.chat

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.inplace.api.ApiImageLoader
import com.inplace.api.vk.ApiVK
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ChatRepo {
    val LOG_TAG = "vkApi"

    private val executor: ExecutorService = Executors.newFixedThreadPool(5)
    private var avatarLiveData: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()

    fun getAvatar() = avatarLiveData

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
}

