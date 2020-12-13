package com.inplace.models

import androidx.room.TypeConverter

enum class Source {
    VK,
    TELEGRAM,
}

class SourceConverter {
    @TypeConverter
    fun fromSource(source: Source): String {
        return source.toString()
    }

    @TypeConverter
    fun toSource(data: String): Source {
        return when (data) {
            "TELEGRAM" -> Source.TELEGRAM
            else -> Source.VK
        }
    }
}