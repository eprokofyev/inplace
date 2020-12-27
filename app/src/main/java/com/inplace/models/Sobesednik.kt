package com.inplace.models

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


interface IVKSobesednik {
    var vk: VKSobesednik?
}

interface ITelegramSobesednik {
    var telegram: TelegramSobesednik?
}

@Entity()
@Parcelize
data class VKSobesednik(
    @PrimaryKey var userID: Long = 0,
    var name: String = "",
    var lastName: String = "",
    @Ignore var avatar: Bitmap? = null,
    var avatarUrl: String = "",
    var activeTime: String = "",
    var about: String = "",
    var createdAT: Long = 0
) : Parcelable

@Entity
data class TelegramSobesednik(
    @PrimaryKey var userID: Long,
    var name: String,
    var lastName: String,
    @Ignore var avatar: Bitmap?,
    var avatarUrl: String,
    var login: String,
    var mobile: String,
    var activeTime: String,
    var about: String,
    var createdAT: Long = 0
)

data class SuperSobesednik(
    override var vk: VKSobesednik?,
    override var telegram: TelegramSobesednik?,
    var defaultSource: Source,
) : IVKSobesednik, ITelegramSobesednik

@Parcelize
data class SimpleSobesednik(
    val vkID: Long,
    val telegramID: Long,
    var defaultSource: @RawValue Source,
) : Parcelable