package fr.xebia.xebicon.vote.model

import android.os.Parcel
import android.os.Parcelable

data class ObstacleInfo(val blocked: Boolean, val obstacleType: String) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ObstacleInfo> = object : Parcelable.Creator<ObstacleInfo> {
            override fun createFromParcel(source: Parcel): ObstacleInfo = ObstacleInfo(source)
            override fun newArray(size: Int): Array<ObstacleInfo?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(1.equals(source.readInt()), source.readString())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt((if (blocked) 1 else 0))
        dest?.writeString(obstacleType)
    }
}