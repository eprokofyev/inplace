package com.inplace.chats

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inplace.models.Chat
import com.inplace.models.User

class ChatsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: ChatsRepo = ChatsRepo(application)
    private var mChats: MutableLiveData<MutableList<Chat>> = repo.getChats()

    fun getChats() = mChats

    fun refresh() {
        repo.refresh()
    }
}