package com.inplace.api.vk

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

data class VkUser(
    var id: Long = 0,
    var firstName: String = "",
    var lastName: String = "",
    var about: String = "",
    var status: String = "",
    var photo200Square: String = "",
    var online: Boolean = false,
    var isClosed: Boolean = false,
    var deactivated: Boolean = false) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(about)
        parcel.writeString(photo200Square)
        parcel.writeByte(if (isClosed) 1 else 0)
        parcel.writeByte(if (isClosed) 1 else 0)
        parcel.writeByte(if (deactivated) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VkUser> {
        override fun createFromParcel(parcel: Parcel): VkUser {
            return VkUser(parcel)
        }

        override fun newArray(size: Int): Array<VkUser?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject)
                = VkUser(id = json.optLong("id", 0),
            firstName = json.optString("first_name", ""),
            lastName = json.optString("last_name", ""),
            about = json.optString("about", ""),
            status = json.optString("status", ""),
            photo200Square = json.optString("photo_200", ""),
            online = json.optBoolean("online", false),
            isClosed = json.optBoolean("is_closed", false),
            deactivated = json.optBoolean("deactivated", false))
    }
}