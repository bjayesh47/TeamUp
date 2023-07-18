package `in`.walnutlabs.teamup.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import `in`.walnutlabs.teamup.R

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

        if (validateForm(name, email, password)) {
            showProgressDialog(resources.getString(R.string.progress_dialog_wait))
            FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail: String = firebaseUser.email!!
                        Toast.makeText(
                            this,
                            resources.getString(R.string.toast_account_creation_successful),
                            Toast.LENGTH_SHORT
                        ).show()
                        FirebaseAuth.getInstance().signOut()
                        finish()
                    }
                    else {
                        task.exception!!.message?.let { showErrorSnackBar(it) }
                    }
                }
        }
    }
    private fun validateForm(
        name: String,
        email: String,
        password: String
    ): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar(resources.getString(R.string.missing_field_name))
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar(resources.getString(R.string.missing_field_email))
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar(resources.getString(R.string.missing_field_password))
                false
            }
            !checkName(name) -> {
                showErrorSnackBar(resources.getString(R.string.incorrect_field_name))
                false
            }
            !checkEmail(email) -> {
                showErrorSnackBar(resources.getString(R.string.incorrect_field_email))
                false
            }
            else -> true
        }
    }

    private fun checkName(name: String): Boolean {
        if (name.length in 3..20) {
            for (char in name) {
                if (char !in 'a'..'z' && char !in 'A'..'Z')
                    return false
            }
            return true
        }
        return false
    }

    private fun checkEmail(email: String): Boolean {
        val pattern: String = "[a-z0-9]+@[a-z]+.[a-z]{2,3}"
        val regex: Regex = Regex(pattern)

        return email.matches(regex)
    }
}