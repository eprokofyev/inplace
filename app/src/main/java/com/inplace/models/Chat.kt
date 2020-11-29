package com.inplace.models

import android.graphics.Bitmap

data class Chat(val user: User,
                var title: String,
                var avatar: Bitmap?,
                var sobesedniks: MutableList<Sobesednik>,
                var messages: MutableList<Message>,
                var isHeard: Boolean,
                var isPrivate: Boolean,
                val conversationVkId: String,
                var conversationTelegramId: String,
                var defaultMessenger: Source,
                val localId: String
)