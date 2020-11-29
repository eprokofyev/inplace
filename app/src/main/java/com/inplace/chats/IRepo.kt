package com.inplace.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.inplace.models.Chat

interface IRepo {
    fun getChats(): MutableLiveData<MutableList<Chat>>
}