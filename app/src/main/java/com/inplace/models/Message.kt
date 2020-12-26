package com.inplace.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Entity(
        tableName = "messages",
        primaryKeys = arrayOf("message_id", "user_id", "chat_id", "from_messenger")
)
@TypeConverters(SourceConverter::class, PhotosConverter::class, MessageStatusConverter::class)
@Parcelize
data class Message(
        @ColumnInfo(name = "message_id") var messageID: Int = 0,
        var date: Long = 0,
        var text: String = "",
        @ColumnInfo(name = "user_id") var userID: Long = 0,
        @ColumnInfo(name = "chat_id") var chatID: Long = 0,
        var myMsg: Boolean = false,
        @ColumnInfo(name = "from_messenger") var fromMessenger: Source = Source.VK,
        var status: MessageStatus = MessageStatus.SENT,
        var isRead: Boolean = false,
        var photos: @RawValue ArrayList<String> = arrayListOf(),
) : Parcelable

class PhotosConverter {
    @TypeConverter
    fun fromPhotos(photos: ArrayList<String?>?): String? {
        return photos?.filterNotNull()?.joinToString(separator = " ")
    }

    @TypeConverter
    fun toPhotos(data: String?): ArrayList<String> {
        val list = data?.split(" ") ?: listOf()
        if (list.isNotEmpty() && list[0] != "")
            return ArrayList(list)
        return arrayListOf()
    }
}