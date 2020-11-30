package com.inplace.chats

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.inplace.chat.ChatModel
import com.inplace.chat.ChatPagingSource
import com.inplace.chat.ChatsPagingDataSource
import com.inplace.chat.DateParser
import com.inplace.models.Chat
import com.inplace.models.Message
import com.inplace.models.User

class ChatsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: ChatsRepo = ChatsRepo()

    fun getChats() = getChatsListStream()

    private fun getChatsListStream() =
        Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                prefetchDistance = 5,
            )
        ) {
            ChatsPagingDataSource(repo, getApplication())
        }.liveData

}