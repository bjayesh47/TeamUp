package `in`.walnutlabs.teamup.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import `in`.walnutlabs.teamup.R

class TaskListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        setUpActionBar()
    }

    private fun setUpActionBar() {

    }
}