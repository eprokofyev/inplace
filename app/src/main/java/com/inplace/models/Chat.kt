package com.inplace.models

data class Chat(val user: User,
                var sobesedniks: MutableList<Sobesednik>,
                var messages: MutableList<Message>,
                var isHeard: Boolean,
                val conversationVkId: String,
                var conversationTelegramId: String,
                var defaultMessenger: Source,
                val localId: String
)