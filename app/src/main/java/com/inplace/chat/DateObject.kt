package com.inplace.chat

import com.inplace.models.Message
import java.util.*

class DateObject: ListObject() {
    lateinit var date: String
    override fun getType(): Int = MessageType.DATE

}