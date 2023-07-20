package `in`.walnutlabs.teamup.activities

import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import `in`.walnutlabs.teamup.R
import `in`.walnutlabs.teamup.firebase.FireStore
import `in`.walnutlabs.teamup.models.User

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        findViewById<ConstraintLayout>(R.id.clCreateAccount)
            .setOnClickListener { registerUser() }
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.tbSignUpActivity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        actionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back_black)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun registerUser() {
        val name: String = findViewById<EditText>(R.id.etNameInput)
            .text.toString().trim { it <= ' ' }
        val email: String = findViewById<EditText>(R.id.etEmailInput)
            .text.toString().trim { it <= ' ' }
        val password: String = findViewById<EditText>(R.id.etPasswordInput)
            .text.toString().trim { it <= ' ' }

        if (validateSignUp(name, email, password)) {
            showProgressDialog(resources.getString(R.string.progress_dialog_wait))
            FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail: String = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, email)
                        FireStore().registerUser(this@SignUpActivity, user)
                    }
                    else {
                        task.exception!!.message?.let { showErrorSnackBar(it) }
                    }
                }
        }
    }

    fun registerUserSuccess() {
        Toast.makeText(
            this,
            resources.getString(R.string.toast_account_creation_successful),
            Toast.LENGTH_SHORT
        ).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}