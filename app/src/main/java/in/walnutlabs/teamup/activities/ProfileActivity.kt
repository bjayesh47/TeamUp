package `in`.walnutlabs.teamup.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import `in`.walnutlabs.teamup.R
import `in`.walnutlabs.teamup.firebase.FireStore
import `in`.walnutlabs.teamup.models.User

class ProfileActivity : BaseActivity() {

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE: Int = 1
        private const val PICK_IMAGE_REQUEST_CODE: Int = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupActionBar()

        FireStore().loadUser(this@ProfileActivity)

        findViewById<CircleImageView>(R.id.civProfilePic).setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@ProfileActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {

            }
            else {
                ActivityCompat.requestPermissions(
                    this@ProfileActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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
        Glide
            .with(this@ProfileActivity)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById<CircleImageView>(R.id.civProfilePic))
        findViewById<EditText>(R.id.etNameInput).setText(loggedInUser.name)
        findViewById<EditText>(R.id.etEmailInput).setText(loggedInUser.email)
        if (loggedInUser.bio.isNotEmpty()) {
            findViewById<EditText>(R.id.etBioInput).setText(loggedInUser.bio)
        }
    }
}