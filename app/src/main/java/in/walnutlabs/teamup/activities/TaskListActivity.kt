package `in`.walnutlabs.teamup.activities

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.walnutlabs.teamup.R
import `in`.walnutlabs.teamup.adapters.TaskListAdapter
import `in`.walnutlabs.teamup.firebase.FireStore
import `in`.walnutlabs.teamup.models.Board
import `in`.walnutlabs.teamup.models.Card
import `in`.walnutlabs.teamup.models.Task
import `in`.walnutlabs.teamup.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var boardDetails: Board
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

    private fun setUpActionBar() {
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.tbTaskListActivity)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar

        actionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back_white)
            it.title= boardDetails.name
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    fun boardDetails(board: Board) {
        boardDetails = board
        hideProgressDialog()
        setUpActionBar()

        val addTaskList: Task = Task(resources.getString(R.string.task_list_screen_add_list))
        board.taskList.add(addTaskList)

        with (findViewById<RecyclerView>(R.id.rvTaskList)) {
            layoutManager = LinearLayoutManager(
                this@TaskListActivity,
                LinearLayoutManager.HORIZONTAL,
                false)
            val adapter: TaskListAdapter = TaskListAdapter(this@TaskListActivity, board.taskList)
            this.adapter = adapter
            setHasFixedSize(true)
        }
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.progress_dialog_wait))
        FireStore().getBoardDetails(this@TaskListActivity, boardDetails.documentID)
    }

    fun createTaskList(taskListName: String) {
        val task: Task = Task(taskListName, FireStore().getCurrentUserID())

        boardDetails.taskList.add(0, task)
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.progress_dialog_wait))
        FireStore().addUpdateTaskListActivity(this@TaskListActivity, boardDetails)
    }

    fun updateTaskList(position: Int, listName: String, model: Task) {
        val task: Task = Task(listName, model.createdBy)

        boardDetails.taskList[position] = task
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.progress_dialog_wait))
        FireStore().addUpdateTaskListActivity(this@TaskListActivity, boardDetails)
    }


    fun deleteTaskList(position: Int) {
        boardDetails.taskList.removeAt(position)
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.progress_dialog_wait))
        FireStore().addUpdateTaskListActivity(this@TaskListActivity, boardDetails)
    }

    fun addCardToTaskList(position: Int, cardName: String) {
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FireStore().getCurrentUserID())

        val card: Card = Card(cardName, FireStore().getCurrentUserID(), cardAssignedUsersList)

        val cardsList: ArrayList<Card> = boardDetails.taskList[position].cards
        cardsList.add(card)

        val task: Task = Task(
            boardDetails.taskList[position].title,
            boardDetails.taskList[position].createdBy,
            cardsList
        )

        boardDetails.taskList[position]= task

        showProgressDialog(resources.getString(R.string.progress_dialog_wait))
        FireStore().addUpdateTaskListActivity(this@TaskListActivity, boardDetails)
    }
}