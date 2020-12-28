package com.inplace.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.inplace.db.AppDatabase
import com.inplace.models.Message

class ChatViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var chatRepo = ChatRepo(getApplication())
    private var mAvatar = chatRepo.getAvatar()
    private val refreshMessage = chatRepo.getRefreshMessageLiveData()

    @ExperimentalPagingApi
    fun getMessages(id: Long) = getMessagesListStream(id)
        .map { value: PagingData<Message> ->
            value.map { message: Message ->
                ChatModel.MessageItem(message)
            }
        }.map {
            it.insertSeparators { before, after ->
                if (after == null) {
                    return@insertSeparators null
                }

                if (before == null) {
                    return@insertSeparators null
                }

                if (DateParser.getDateAsUnix(before.message.date) > DateParser.getDateAsUnix(after.message.date)) {
                    ChatModel.DateItem(DateParser.convertDateToString(before.message.date))
                } else {
                    return@insertSeparators null
                }
            }
        }

    private val database = AppDatabase.getInstance(getApplication())
    private val messageDao = database.getMessageDao()

    @ExperimentalPagingApi
    private fun getMessagesListStream(chatID: Long) =
        Pager(
            PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5,
            ),
            remoteMediator = ChatMediator(chatID, chatRepo, database),
            pagingSourceFactory = { messageDao.pagingSource(chatID) }
        ).liveData.cachedIn(viewModelScope)

    fun getAvatar() = mAvatar

    fun getRefreshMessage() = refreshMessage

    fun fetchAvatar(url: String) {
        chatRepo.fetchAvatar(url)
    }

    fun sendMessage(position: Int, message: Message) {
        chatRepo.sendMessage(position, message)
    }

    fun insertMessages(messages: List<Message>){
        chatRepo.insertMessages(messages)
    }

    fun markChatAsRead(chatID: Long){
        chatRepo.markChatAsRead(chatID)
    }

    fun updateOutRead(newOutRead: Int, chatID: Long) {
        chatRepo.updateOutRead(newOutRead,chatID)
    }
}