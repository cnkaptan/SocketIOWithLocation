package com.cnkaptan.socketiowithlocation.model

import android.os.Parcel
import android.os.Parcelable

data class LoginResponseData(val car_id: Int,
                             val driver_id: Int,
                             val order: String?,
                             val status: String = "READY",
                             val uid: String) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(car_id)
        writeInt(driver_id)
        writeString(order)
        writeString(status)
        writeString(uid)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<LoginResponseData> = object : Parcelable.Creator<LoginResponseData> {
            override fun createFromParcel(source: Parcel): LoginResponseData = LoginResponseData(source)
            override fun newArray(size: Int): Array<LoginResponseData?> = arrayOfNulls(size)
        }
    }
}