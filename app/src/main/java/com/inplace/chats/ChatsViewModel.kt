package com.inplace.chats

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.inplace.chat.ChatsPagingDataSource

class ChatsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: ChatsRepo = ChatsRepo()

    fun getChats() = getChatsListStream()

    private fun getChatsListStream() =
        Pager(
            PagingConfig(
                pageSize = 12,
                enablePlaceholders = true,
                prefetchDistance = 6,
            )
        ) {
            ChatsPagingDataSource(repo, getApplication())
        }.liveData

}