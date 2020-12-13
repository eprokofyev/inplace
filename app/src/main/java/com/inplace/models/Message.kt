package com.inplace.models

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

@Entity(indices = arrayOf(
        Index(value = ["message_id", "user_id", "chat_id", "source"],
        unique = true)
))
@Parcelize
data class Message(
        @ColumnInfo(name = "message_id") var messageID: Int,
        var date: Long,
        var text: String,
        @ColumnInfo(name = "user_id") var userID: Long,
        @ColumnInfo(name = "chat_id") var chatID: Long,
        var myMsg: Boolean,
        @ColumnInfo(name = "source") @TypeConverters(SourceConverter::class) var fromMessenger: @RawValue Source,
        var isRead: Boolean,
        @TypeConverters(PhotosConverter::class) var photos: @RawValue ArrayList<String>,
) : Parcelable

class PhotosConverter {
        @TypeConverter
        fun fromPhotos(photos: ArrayList<String>): String {
                return photos.joinToString(separator = " ")
        }

        @TypeConverter
        fun toPhotos(data: String): ArrayList<String> {
                return ArrayList(data.split(" "))
        }
}