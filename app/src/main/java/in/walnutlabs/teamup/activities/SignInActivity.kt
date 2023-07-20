package `in`.walnutlabs.teamup.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import `in`.walnutlabs.teamup.R

class SignInActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        auth = FirebaseAuth.getInstance()

        findViewById<ConstraintLayout>(R.id.clSignIn)
            .setOnClickListener { signInRegisteredUser() }
    }

    private fun signInRegisteredUser() {
        val email: String = findViewById<EditText>(R.id.etEmailInput)
            .text.toString().trim { it <= ' ' }
        val password: String = findViewById<EditText>(R.id.etPasswordInput)
            .text.toString().trim { it <= ' ' }

        if (validateSignIn(email, password)) {
            showProgressDialog(resources.getString(R.string.progress_dialog_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { taskResult ->
                    hideProgressDialog()
                    if (taskResult.isSuccessful) {
                        val user: FirebaseUser? = auth.currentUser
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    }
                    else {
                        taskResult.exception!!.message?.let { showErrorSnackBar(it) }
                    }
                }
        }
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.tbSignInActivity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        actionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back_black)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}