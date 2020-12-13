package com.inplace.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class SuperUser(
    val name: String,
    val lastName: String,
    var avatarURL: String,
    val vk: @RawValue VKUser?,
    val telegram: @RawValue TelegramUser?,
) : Parcelable

@Parcelize
data class VKUser(
    val userID: Long,
    val name: String,
    val lastName: String,
    var avatarURL: String,
    val mobile: String = "",
    val email: String = "",
    var createdAT: Long = 0
) : Parcelable

@Parcelize
data class TelegramUser(
    val userID: Long,
    val name: String,
    val lastName: String,
    var login: String,
    val mobile: String,
    var avatarURL: String,
    var createdAT: Long = 0
) : Parcelable


@Parcelize
data class SimpleUser(
    val name: String,
    val lastName: String,
    var avatarURL: String,
    val vkID: Long,
    val telegramID: Long,
) : Parcelable