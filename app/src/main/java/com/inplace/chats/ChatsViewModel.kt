package com.inplace.chats

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.*
import androidx.paging.*
import com.inplace.chat.ChatsPagingDataSource
import com.inplace.chats.data.vk.VKRepository
import com.inplace.models.SuperChat
import com.inplace.models.TelegramChat
import com.inplace.models.VKChat
import kotlinx.coroutines.CoroutineScope

@ExperimentalPagingApi
class ChatsViewModel(application: Application) : AndroidViewModel(application) {

    val repository: VKRepository = VKRepository.getInstance(getApplication())

    private var vkChats: MutableLiveData<MutableList<VKChat>> = MutableLiveData<MutableList<VKChat>>()
    private val telegramChats: MutableLiveData<MutableList<TelegramChat>> = MutableLiveData<MutableList<TelegramChat>>()
    private val avatars: MutableLiveData<MutableList<Bitmap>> = MutableLiveData<MutableList<Bitmap>>()

    private var chats = repository.letVKChatsLiveData().cachedIn(viewModelScope)



    fun getChats() = chats.map { it.map {  SuperChat(
        it.title,
        it.avatarUrl,
        it.lastMessage,
        true,
        arrayListOf(it),
        arrayListOf(),
        it.chatID,
        it.chatID
    )  } }



}