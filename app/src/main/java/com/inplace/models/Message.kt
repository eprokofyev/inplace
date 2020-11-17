package com.inplace.models

data class Message(var text: String,
                   var time: String,
                   var source: Messenger,
                   var sobesedniks: Sobesednik,
                   var isRead: Boolean,
                   var conversationId: String,
                   var id: String,
                   var localId: String,
)