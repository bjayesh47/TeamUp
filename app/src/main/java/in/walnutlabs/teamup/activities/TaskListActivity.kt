package `in`.walnutlabs.teamup.activities

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import `in`.walnutlabs.teamup.R
import `in`.walnutlabs.teamup.firebase.FireStore
import `in`.walnutlabs.teamup.models.Board
import `in`.walnutlabs.teamup.utils.Constants

class TaskListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        var boardDocumentID: String = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentID = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.progress_dialog_wait))
        FireStore().getBoardDetails(this@TaskListActivity, boardDocumentID)
    }

    private fun setUpActionBar(title: String) {
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.tbTaskListActivity)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar

        actionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back_white)
            it.title= title
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    fun boardDetails(board: Board) {
        hideProgressDialog()
        setUpActionBar(board.name)
    }
}