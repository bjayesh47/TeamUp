package `in`.walnutlabs.teamup.adapters

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Audio.Playlists.Members
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.walnutlabs.teamup.R
import `in`.walnutlabs.teamup.activities.BaseActivity
import `in`.walnutlabs.teamup.firebase.FireStore
import `in`.walnutlabs.teamup.models.Board
import `in`.walnutlabs.teamup.models.User
import `in`.walnutlabs.teamup.utils.Constants

class MembersActivity : BaseActivity() {

    private var boardDetails: Board? = null
    private var assignedMembersList: ArrayList<User>? = null
    private var changesMade: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if (intent.hasExtra(Constants.BOARD_DETAILS))
            boardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAILS)


        setupActionBar()
        boardDetails?.let {
            showProgressDialog(resources.getString(R.string.progress_dialog_wait))
            FireStore().getAssignedMemberList(this@MembersActivity, it.assignedTo)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_member_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionAddMembers -> showSearchDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSearchDialog() {
        val dialog: Dialog = Dialog(this@MembersActivity)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<ConstraintLayout>(R.id.clSearch).setOnClickListener {
            val email: String = dialog.findViewById<EditText>(R.id.etEmailInput).text.toString()
            if (validateSignIn(email, "password")) {
                showProgressDialog(resources.getString(R.string.progress_dialog_wait))
                FireStore().getMemberDetails(this@MembersActivity, email)
                dialog.dismiss()
            }
        }
        dialog.findViewById<ConstraintLayout>(R.id.clCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.tbMembersActivity)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar

        actionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back_white)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        if (changesMade) setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

     fun setUpMemberList(list: ArrayList<User>) {
         assignedMembersList = list
         hideProgressDialog()
         with (findViewById<RecyclerView>(R.id.rvMemberList)) {
             layoutManager = LinearLayoutManager(this@MembersActivity)
             setHasFixedSize(true)
             adapter = MembersListAdapter(this@MembersActivity, list)
         }
    }

    fun memberDetails(user: User) {
        boardDetails?.let {
            it.assignedTo.add(user.id)
            FireStore().assignMembersToBoard(this@MembersActivity, it, user)
        }
    }

    fun memberAssignSuccess(user: User) {
        hideProgressDialog()
        assignedMembersList?.let {
            it.add(user)
            setUpMemberList(it)
            changesMade = true
        }
    }
}