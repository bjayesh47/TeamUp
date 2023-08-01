package `in`.walnutlabs.teamup.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import `in`.walnutlabs.teamup.R
import `in`.walnutlabs.teamup.firebase.FireStore
import `in`.walnutlabs.teamup.models.Board
import `in`.walnutlabs.teamup.utils.Constants

class CreateBoardActivity : BaseActivity() {

    private var boardImageSelectedFromDevice: Uri? = null
    private var boardImageURL: String = ""
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        setupActionBar()

        if (intent.hasExtra(Constants.NAME)) {
            username = intent.getStringExtra(Constants.NAME).toString()
        }

        findViewById<CircleImageView>(R.id.civBoardImage).setOnClickListener {
            Constants.showImageChooser(this@CreateBoardActivity)
        }

        findViewById<ConstraintLayout>(R.id.clCreateBoard).setOnClickListener {
            if (boardImageSelectedFromDevice != null) {
                uploadBoardImage()
                boardImageSelectedFromDevice = null
            }
            else {
                showProgressDialog(resources.getString(R.string.progress_dialog_wait))
                createBoard()
            }
        }
    }

    private fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.progress_dialog_wait))
        boardImageSelectedFromDevice?.let {
            val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child(
                "BOARD_IMAGE" + System.currentTimeMillis() + "."
                + Constants.getFileExtension(this@CreateBoardActivity, it)
            )

            storageReference.putFile(it)
                .addOnSuccessListener { taskSnapshot ->
                    Log.i(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl!!.toString()
                    )

                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.i("Downloadable URL", uri.toString())
                        boardImageURL = uri.toString()
                        createBoard()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@CreateBoardActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }
        }
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.tbCreateBoardActivity)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar

        actionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back_white)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
            data?.data?.let { uri ->
                boardImageSelectedFromDevice = uri
                Glide
                    .with(this@CreateBoardActivity)
                    .load(uri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_glide_placeholder)
                    .into(findViewById<CircleImageView>(R.id.civBoardImage))
            }
        }
    }

    private fun createBoard() {
        val assignedUserList: ArrayList<String> = ArrayList()
        assignedUserList.add(getCurrentUserID())

        val board: Board = Board(
            findViewById<EditText>(R.id.etBoardName).text.toString(),
            boardImageURL,
            username,
            assignedUserList
        )

        FireStore().registerBoard(this@CreateBoardActivity, board)
    }

    fun boardCreatedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}