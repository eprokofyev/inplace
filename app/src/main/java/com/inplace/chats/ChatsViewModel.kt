package com.inplace.chats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inplace.models.Chat
import com.inplace.models.User

class ChatsViewModel(repo: IRepo, user: User) : ViewModel() {
    private val repo: IRepo = repo
    private var mChats: MutableLiveData<MutableList<Chat>> = repo.getChats()

    fun getChats() = mChats
}