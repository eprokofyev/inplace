package com.inplace.chat.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_remote_keys")
data class ChatRemoteKey(
    @PrimaryKey @ColumnInfo(name = "chat_id") val chatID: Long,
    @ColumnInfo(name = "next_key") val nextKey: Int?
)