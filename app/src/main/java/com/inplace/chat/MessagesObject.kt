package com.inplace.chat

import com.inplace.models.Message

class MessagesObject: ListObject() {
    lateinit var message: Message

    override fun getType(): Int = if (message.myMsg) MessageType.HOST else MessageType.TARGET
}