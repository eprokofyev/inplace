package com.inplace.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.map
import androidx.paging.*
import com.inplace.models.Message

class ChatViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var chatRepo = ChatRepo()
    private var mAvatar = chatRepo.getAvatar()

    fun getMessages(id: Int) = getMessagesListStream(id)
        .map { value: PagingData<Message> ->
            value.map { message: Message ->
                ChatModel.MessageItem(
                    message
                )
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

    private fun getMessagesListStream(conversationId: Int) =
        Pager(
            PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 2,
            )
        ) {
            ChatPagingSource(chatRepo,conversationId)
        }.liveData


    fun getAvatar() = mAvatar

    fun fetchAvatar(url: String) {
        chatRepo.fetchAvatar(url, getApplication())
    }

    fun sendMessage(conversationId: Int, message: String) {
        chatRepo.sendMessage(conversationId, message)
    }
}