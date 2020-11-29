package com.inplace.chat

import com.inplace.models.Message

sealed class ChatModel {
    data class DateItem(val date: String) : ChatModel()
    data class MessageItem(val message: Message) : ChatModel()
}
