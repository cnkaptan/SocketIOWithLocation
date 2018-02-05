package com.cnkaptan.socketiowithlocation.model

import android.os.Parcel
import android.os.Parcelable

data class LoginResponse(val data: LoginResponseData, val status: String = "true") : Parcelable {
    constructor(source: Parcel) : this(
            source.readParcelable<LoginResponseData>(LoginResponseData::class.java.classLoader),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(data, 0)
        writeString(status)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<LoginResponse> = object : Parcelable.Creator<LoginResponse> {
            override fun createFromParcel(source: Parcel): LoginResponse = LoginResponse(source)
            override fun newArray(size: Int): Array<LoginResponse?> = arrayOfNulls(size)
        }
    }
}