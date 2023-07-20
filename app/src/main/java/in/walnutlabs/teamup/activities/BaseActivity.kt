package `in`.walnutlabs.teamup.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import `in`.walnutlabs.teamup.R

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce: Boolean = false
    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(text: String) {
        progressDialog = Dialog(this)

        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.findViewById<TextView>(R.id.tvProgressText).text = text

        progressDialog.show()
    }

    fun hideProgressDialog() {
        progressDialog.dismiss()
    }

    fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.toast_exit),
            Toast.LENGTH_SHORT
        ).show()

        Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    fun showErrorSnackBar(message: String) {
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG)
        snackBar.setBackgroundTint(resources.getColor(R.color.snack_bar_error))
        snackBar.show()
    }

    protected fun validateSignUp(
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

    protected fun validateSignIn(
        email: String,
        password: String
    ): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar(resources.getString(R.string.missing_field_email))
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar(resources.getString(R.string.missing_field_password))
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