package com.inplace.chat

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.inplace.models.Message

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private var mRepo = ChatRepo()
    private var mMessages: MutableLiveData<List<Message>> = mRepo.getMessages()
    private var mAvatar = mRepo.getAvatar()

    fun getMessages() = mMessages

    fun fetchMessages(conversationId: Int, start: Int, end: Int) {
        mRepo.fetchMessages(conversationId, start, end)
    }

    fun getAvatar() = mAvatar

    fun fetchAvatar(url: String){
        mRepo.fetchAvatar(url, getApplication())
    }

}