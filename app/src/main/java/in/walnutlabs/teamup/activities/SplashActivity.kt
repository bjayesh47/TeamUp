package `in`.walnutlabs.teamup.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import `in`.walnutlabs.teamup.R
import `in`.walnutlabs.teamup.firebase.FireStore

class SplashActivity:BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({
            if (FireStore().getCurrentUserID().isNotEmpty()) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            else {
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }
            finish()
        }, 1000)
    }
}