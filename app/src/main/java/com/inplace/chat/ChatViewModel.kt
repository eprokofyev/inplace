package com.inplace.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private var chatRepo = ChatRepo()
//    private var mMessages: MutableLiveData<List<Message>> = chatRepo.getMessages()

    private var mAvatar = chatRepo.getAvatar()


    private var getMessageQuery = MutableLiveData<Int>()

    val messages = getMessageQuery.switchMap { conversationId ->
        chatRepo.getMessages(conversationId)

    }

    fun getMessages(conversationId: Int) {
        getMessageQuery.value = conversationId
    }

    fun getAvatar() = mAvatar

    fun fetchAvatar(url: String) {
        chatRepo.fetchAvatar(url, getApplication())
    }

    fun sendMessage(conversationId: Int, message: String) {
        chatRepo.sendMessage(conversationId, message)
    }

}