package com.inplace.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.inplace.models.Message

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private var mRepo = MessageRepo(getApplication())
    private var mMessages: MutableLiveData<List<Message>> = mRepo.getMessages()

    fun getMessages() = mMessages

    fun fetchMessages() {
        mRepo.fetchMessages()
    }


}