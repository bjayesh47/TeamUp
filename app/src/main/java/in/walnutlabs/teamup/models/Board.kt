package `in`.walnutlabs.teamup.models

import android.os.Parcel
import android.os.Parcelable

data class Board(
    val name: String,
    val image: String,
    val createdBy: String,
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentID: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!
    ) {}

    override fun describeContents(): Int  = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with (parcel) {
            writeString(name)
            writeString(image)
            writeString(createdBy)
            writeStringList(assignedTo)
            writeString(documentID)
        }
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }

}
