package com.inplace.models

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

@Entity(primaryKeys = arrayOf("message_id", "user_id", "chat_id", "from_messenger")
)
@TypeConverters(SourceConverter::class, PhotosConverter::class)
@Parcelize
data class Message(
        @ColumnInfo(name = "message_id") var messageID: Int = 0,
        var date: Long = 0,
        var text: String = "",
        @ColumnInfo(name = "user_id") var userID: Long = 0,
        @ColumnInfo(name = "chat_id") var chatID: Long = 0,
        var myMsg: Boolean = false,
        @ColumnInfo(name = "from_messenger") var fromMessenger: Source = Source.VK,
        var isRead: Boolean = false,
        var photos: @RawValue ArrayList<String> = arrayListOf(),
) : Parcelable

class PhotosConverter {
        @TypeConverter
        fun fromPhotos(photos: ArrayList<String?>?): String? {
                return photos?.filterNotNull()?.joinToString(separator = " ") ?: ""
        }

        @TypeConverter
        fun toPhotos(data: String?): ArrayList<String> {
                return ArrayList(data?.split(" ") ?: listOf())
        }
}