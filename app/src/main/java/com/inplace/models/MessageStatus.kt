package com.inplace.models

import android.os.Parcelable
import androidx.room.TypeConverter
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class MessageStatus : Parcelable {
    ERROR, SENDING, SENT, READ
}

object MessageStatusConverter {

    @TypeConverter
    @JvmStatic
    fun fromMessageStatus(messageStatus: MessageStatus): String {
        return messageStatus.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toMessageStatus(data: String): MessageStatus {
        return when (data) {
            "ERROR" -> MessageStatus.ERROR
            "SENDING" -> MessageStatus.SENDING
            "SENT" -> MessageStatus.SENT
            else -> MessageStatus.READ
        }
    }
}