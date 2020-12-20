package com.inplace.models

import android.os.Parcelable
import androidx.room.TypeConverter
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class Source : Parcelable {
    VK,
    TELEGRAM,
}

object SourceConverter {

    @TypeConverter
    @JvmStatic
    fun fromSource(source: Source?): String? {
        return source.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toSource(data: String?): Source {
        return when (data) {
            "TELEGRAM" -> Source.TELEGRAM
            else -> Source.VK
        }
    }
}