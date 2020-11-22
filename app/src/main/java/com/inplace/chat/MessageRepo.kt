package com.inplace.chat

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.inplace.models.Message

class MessageRepo(private var context: Context) {
    lateinit var dataSet: MutableLiveData<List<Message>>

    init {
        dataSet.value = emptyList()
    }


    fun getMessages(): MutableLiveData<List<Message>> {
        return dataSet
    }

    fun fetchMessages() {
        //todo
    }


}

