package com.inplace.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.inplace.models.SuperChat

interface IRepo {
    fun getChats(): MutableLiveData<MutableList<SuperChat>>
}