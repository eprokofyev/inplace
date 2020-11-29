package com.inplace.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Chat(val user:@RawValue  User,
                var sobesedniks:@RawValue MutableList<Sobesednik>,
                var messages:@RawValue MutableList<Message>,
                var isHeard: Boolean,
                val conversationVkId: String,
                var conversationTelegramId: String,
                var defaultMessenger:@RawValue Source,
                val localId: String
):Parcelable