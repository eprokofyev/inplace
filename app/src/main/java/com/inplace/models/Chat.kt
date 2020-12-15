package com.inplace.models

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


enum class ChatType {
    PRIVATE,
    GROUP
}

@Entity
@Parcelize
data class VKChat(
    @PrimaryKey var chatID: Long,
    @ColumnInfo(name = "source") @TypeConverters(SourceConverter::class) val source: Source = Source.VK,
    var title: String,
    @Ignore var avatar: Bitmap?,
    var avatarUrl: String,
    @Ignore var messages:@RawValue MutableList<Message>,
    var isHeard: Boolean,
    var type: @RawValue ChatType,
    @Embedded var lastMessage:@RawValue Message,
    @Ignore var sobesedniks:@RawValue HashMap<Long, IVKSobesednik>,
    var createdAT: Long = 0
) : Parcelable


@Entity
@Parcelize
data class TelegramChat(
    @PrimaryKey var chatID: Long,
    @ColumnInfo(name = "source") @TypeConverters(SourceConverter::class) val source: Source = Source.TELEGRAM,
    var title: String,
    @Ignore var avatar: Bitmap?,
    var avatarUrl: String,
    @Ignore var messages:@RawValue MutableList<Message>,
    var isHeard: Boolean,
    var type: @RawValue ChatType,
    @Embedded var lastMessage:@RawValue Message,
    @Ignore var sobesedniks:@RawValue HashMap<Long, ITelegramSobesednik>,
    var createdAT: Long = 0
) : Parcelable

@Parcelize
data class SuperChat(
    var title: String,
    var avatarURL: String,
    var lastMessage: Message,
    var isHeard: Boolean,
    var vkChats: HashMap<Long, VKChat>,
    var telegramChats: HashMap<Long, TelegramChat>,
    var defaultChatID: Long,
    var currentChat: Long
) : Parcelable

@Parcelize
data class SimpleChat(
    var title: String,
    var avatarURL: String,
    var vkChats:@RawValue List<Long>,
    var telegramChats:@RawValue List<Long>,
    var defaultChatID: Long,
    var isHeard: Boolean,
) : Parcelable