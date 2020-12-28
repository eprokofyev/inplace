package com.inplace.chat


import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.inplace.api.ApiImageLoader
import com.inplace.api.vk.ApiVk
import com.inplace.db.AppDatabase
import com.inplace.models.Message
import com.inplace.models.MessageStatus
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ChatRepo(private val context: Context) {
    val LOG_TAG = "vkApi"

    private val executor: ExecutorService = Executors.newFixedThreadPool(5)
    private val database = AppDatabase.getInstance(context)
    private val messageDao = database.getMessageDao()
    private val avatarLiveData: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()
    private val refreshMessageLiveData: MutableLiveData<Int> = MutableLiveData<Int>()

    fun getRefreshMessageLiveData() = refreshMessageLiveData

    fun getAvatar() = avatarLiveData

    fun fetchAvatar(url: String) {
        executor.execute {
            val imageLoader = ApiImageLoader.getInstance(context)
            val avatarBitmap = imageLoader.getImageByUrl(url)
            avatarLiveData.postValue(avatarBitmap)
        }
    }

    fun sendMessage(position: Int, message: Message) {
        val messageID = message.messageID
        val chatID = message.chatID
        val text = message.text
        val photos = ArrayList(message.photos.map { it.toUri() })
        executor.execute {
            GlobalScope.launch {
                messageDao.insert(message)
                Thread.sleep(200)
                refreshMessageLiveData.postValue(position)
                val result = ApiVk.sendMessage(chatID.toInt(), text, photos)
                if (result.error != null) {
                    Log.d(LOG_TAG, "Error while sending message")
                    messageDao.updateMessageStatus(chatID,messageID,MessageStatus.ERROR)
                } else {
                    Log.d(LOG_TAG, "Message successfully sent")
                    messageDao.updateMessageStatus(chatID,messageID,MessageStatus.SENT)
                    messageDao.updateMessageID(chatID,messageID,result.result)
                }
            }
        }
    }


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
            photos = messagePlain.photos,
            status = when(messagePlain.isRead){
                true -> MessageStatus.READ
                else -> MessageStatus.SENT
            }
        )
    }

    fun markChatAsRead(chatID: Long) {
        executor.execute {
            val result = ApiVk.markAsRead(chatID.toInt())
            Log.d("markAsRead","chat marked as read: ${result.result}")
        }
    }

    fun insertMessages(messages: List<Message>) {
        executor.execute {
            GlobalScope.launch {
                messageDao.insertAll(messages)
                Thread.sleep(200)
                refreshMessageLiveData.postValue(1)
            }
        }
    }

    fun updateOutRead(newOutRead: Int, chatID: Long) {

    }

    fun interface OnChatRequestCompleteListener {
        fun onChatRequestComplete(result: ChatRepoResult)
    }
}

