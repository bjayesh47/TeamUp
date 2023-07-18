package `in`.walnutlabs.teamup.activities

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader.TileMode
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import `in`.walnutlabs.teamup.R


class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        findViewById<ConstraintLayout>(R.id.clSignUp).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        findViewById<ConstraintLayout>(R.id.clSignIn).setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        setShader(findViewById(R.id.tvTitle))
        setShader(findViewById(R.id.tvSignUp))
    }

    private fun setShader(view: TextView) {
        val viewWidth: Float = view.paint.measureText(view.text as String?)

        val headingShader: LinearGradient = LinearGradient(
            0F, 0F, viewWidth, view.textSize,
            intArrayOf(
                resources.getColor(R.color.app_dark_blue),
                resources.getColor(R.color.app_light_blue)
            ),
            floatArrayOf(0.0F, 1.0F),
            TileMode.CLAMP)

        view.paint.shader = headingShader
    }
}