package com.inplace.chats.models

import android.graphics.Bitmap
import com.inplace.models.SuperChat

sealed class ChatsModel {
    data class AvatarItem(val avatar: Avatar) : ChatsModel()
    data class SuperChatItem(val chat: SuperChat) : ChatsModel()
}

data class Avatar(val uri: String, val image: Bitmap)