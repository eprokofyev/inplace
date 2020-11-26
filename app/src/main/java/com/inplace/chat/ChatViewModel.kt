package com.inplace.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.inplace.models.Message

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private var chatRepo = ChatRepo()
    private var mMessages: MutableLiveData<List<Message>> = chatRepo.getMessages()
    private var mAvatar = chatRepo.getAvatar()

    fun getMessages() = mMessages

    fun fetchMessages(conversationId: Int, start: Int, end: Int) {
        chatRepo.fetchMessages(conversationId, start, end)
    }

    fun getAvatar() = mAvatar

    fun fetchAvatar(url: String) {
        chatRepo.fetchAvatar(url, getApplication())
    }

    fun sendMessage(conversationId: Int, message: String) {
        chatRepo.sendMessage(conversationId, message)
    }

}