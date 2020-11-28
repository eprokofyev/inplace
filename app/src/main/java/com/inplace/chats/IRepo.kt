package com.inplace.chats

import androidx.lifecycle.LiveData
import com.inplace.models.Chat

interface IRepo {
    fun getChats(): LiveData<List<Chat>>
}