package com.inplace.models

import java.util.*

data class Message(
        var messageId: Int,
        var date: Long,
        var text: String,
        var fromId: Int,
        var myMsg: Boolean,
        var fromMessenger: Source,
        var photos: ArrayList<String?>? = null,
)