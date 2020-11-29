package com.inplace.models

data class Message(
        var messageId: Int,
        var date: Long,
        var text: String,
        var fromId: Int,
        var myMsg: Boolean,
        var fromMessenger: Source,
)