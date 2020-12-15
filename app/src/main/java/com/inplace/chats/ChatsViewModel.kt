package com.inplace.chats

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.*
import androidx.paging.*
import com.inplace.chat.ChatsPagingDataSource
import com.inplace.models.SuperChat
import com.inplace.models.TelegramChat
import com.inplace.models.VKChat

class ChatsViewModel(application: Application) : AndroidViewModel(application) {

    private var vkChats: MutableLiveData<MutableList<VKChat>> = MutableLiveData<MutableList<VKChat>>()
    private val telegramChats: MutableLiveData<MutableList<TelegramChat>> = MutableLiveData<MutableList<TelegramChat>>()
    private val avatars: MutableLiveData<MutableList<Bitmap>> = MutableLiveData<MutableList<Bitmap>>()


    private val repo: ChatsRepo = ChatsRepo(vkChats, telegramChats, avatars)


    fun getChats() = getChatsListStream()

    private fun getChatsListStream() = Pager(
            PagingConfig(
                pageSize = 12,
                enablePlaceholders = true,
                prefetchDistance = 6,
            )
        ) {
            ChatsPagingDataSource(repo, getApplication())
        }.liveData


}