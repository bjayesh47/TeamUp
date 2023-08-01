package `in`.walnutlabs.teamup.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import `in`.walnutlabs.teamup.activities.CreateBoardActivity

object Constants {
    const val USERS: String = "users"
    const val BOARDS: String = "boards"

    const val NAME: String = "name"
    const val BIO: String = "bio"
    const val IMAGE: String = "image"

    const val ASSIGNED_TO: String = "assignedTo"
    const val DOCUMENT_ID: String = "documentID"

    const val READ_STORAGE_PERMISSION_CODE: Int = 1
    const val PICK_IMAGE_REQUEST_CODE: Int = 2

    fun showImageChooser(activity: Activity) {
        val galleryIntent: Intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(
            galleryIntent,
            PICK_IMAGE_REQUEST_CODE
        )
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap
            .getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}