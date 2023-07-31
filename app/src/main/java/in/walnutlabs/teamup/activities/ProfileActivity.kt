package `in`.walnutlabs.teamup.activities

import android.Manifest
import android.R.id
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import `in`.walnutlabs.teamup.R
import `in`.walnutlabs.teamup.firebase.FireStore
import `in`.walnutlabs.teamup.models.User
import `in`.walnutlabs.teamup.utils.Constants
import java.io.IOException


class ProfileActivity : BaseActivity() {

    private var selectedImageFromDeviceURI: Uri? = null
    private var profileImageURL: String = ""
    private lateinit var userDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupActionBar()

        FireStore().loadUser(this@ProfileActivity)

        findViewById<CircleImageView>(R.id.civProfilePic).setOnClickListener {
            Constants.showImageChooser(this@ProfileActivity)
        }

        findViewById<ConstraintLayout>(R.id.clUpdateAccount).setOnClickListener {
            if (selectedImageFromDeviceURI != null) {
                uploadUserImage()
                selectedImageFromDeviceURI = null
            }
            else {
                showProgressDialog(resources.getString(R.string.progress_dialog_wait))
                updateUserProfileData()
            }
        }
    }

    private fun showExplanation(
        title: String,
        message: String,
        permission: String,
        permissionCode: Int
    ) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@ProfileActivity)
        alertDialog
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                resources.getString(R.string.alert_dialog_positive_btn_text),
                DialogInterface.OnClickListener { _, _ ->
                    requestPermissions(
                        arrayOf(permission),
                        permissionCode
                    )
                }
            )
        alertDialog.create().show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@ProfileActivity)
            }
            else {
                Toast.makeText(
                    this@ProfileActivity,
                    resources.getString(R.string.toast_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data?.data != null) {
            selectedImageFromDeviceURI = data.data
            try {
                Glide
                    .with(this@ProfileActivity)
                    .load(selectedImageFromDeviceURI)
                    .centerCrop()
                    .placeholder(R.drawable.ic_glide_placeholder)
                    .into(findViewById<CircleImageView>(R.id.civProfilePic))
            }
            catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.tbProfileActivity)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar

        actionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back_white)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    fun loadUser(loggedInUser: User) {
        userDetails = loggedInUser
        Glide
            .with(this@ProfileActivity)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_glide_placeholder)
            .into(findViewById<CircleImageView>(R.id.civProfilePic))

        findViewById<EditText>(R.id.etNameInput).setText(loggedInUser.name)
        findViewById<EditText>(R.id.etEmailInput).setText(loggedInUser.email)
        if (loggedInUser.bio.isNotEmpty()) {
            findViewById<EditText>(R.id.etBioInput).setText(loggedInUser.bio)
        }
    }

    private fun updateUserProfileData() {
        var anyChangesMade: Boolean = false
        val userHashmap: HashMap<String, Any> = HashMap()

        if (profileImageURL.isNotEmpty() && profileImageURL != userDetails.image) {
            anyChangesMade = true
            userHashmap[Constants.IMAGE] = profileImageURL
        }

        if (findViewById<EditText>(R.id.etNameInput).text.toString() != userDetails.name) {
            anyChangesMade = true
            userHashmap[Constants.NAME] = findViewById<EditText>(R.id.etNameInput).text.toString()
        }

        if (findViewById<EditText>(R.id.etBioInput).text.toString() != userDetails.bio) {
            anyChangesMade = true
            userHashmap[Constants.BIO] = findViewById<EditText>(R.id.etBioInput).text.toString()
        }

        if (anyChangesMade)
            FireStore().updateUserProfileData(this@ProfileActivity, userHashmap)
    }
    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.progress_dialog_wait))

        if (selectedImageFromDeviceURI != null) {
            val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis()
                        + "." + Constants.getFileExtension(this@ProfileActivity, selectedImageFromDeviceURI)
            )

            storageReference.putFile(selectedImageFromDeviceURI!!)
                .addOnSuccessListener { taskSnapshot ->
                    Log.i(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl!!.toString()
                    )

                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.i("Downloadable Image URL", uri.toString())
                        profileImageURL = uri.toString()
                        updateUserProfileData()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@ProfileActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}