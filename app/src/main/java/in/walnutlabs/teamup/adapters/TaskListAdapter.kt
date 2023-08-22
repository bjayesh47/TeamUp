package `in`.walnutlabs.teamup.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.walnutlabs.teamup.R
import `in`.walnutlabs.teamup.activities.TaskListActivity
import `in`.walnutlabs.teamup.models.Task

open class TaskListAdapter(
    private val context: Context,
    private var list: ArrayList<Task>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val layoutParams: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(16.toDp().toPx(), 0, 32.toDp().toPx(), 0)
        view.layoutParams = layoutParams

        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model: Task = list[position]
        if (holder is TaskViewHolder) {
            with(holder.itemView) {
                if (position == list.size - 1) {
                    findViewById<ConstraintLayout>(R.id.clTaskListAddition).visibility = View.VISIBLE
                    findViewById<ConstraintLayout>(R.id.clCardListAddition).visibility = View.GONE
                }
                else {
                    findViewById<ConstraintLayout>(R.id.clTaskListAddition).visibility = View.GONE
                    findViewById<ConstraintLayout>(R.id.clCardListAddition).visibility = View.VISIBLE
                }

                findViewById<TextView>(R.id.tvTaskListName).text = model.title

                findViewById<TextView>(R.id.tvAddTaskList).setOnClickListener {
                    findViewById<TextView>(R.id.tvAddTaskList).visibility = View.GONE
                    findViewById<ConstraintLayout>(R.id.clAddTaskList).visibility = View.VISIBLE
                }

                findViewById<ImageButton>(R.id.ibCloseAddList).setOnClickListener {
                    findViewById<TextView>(R.id.tvAddTaskList).visibility = View.VISIBLE
                    findViewById<ConstraintLayout>(R.id.clAddTaskList).visibility = View.GONE
                }

                findViewById<ImageButton>(R.id.ibCheckAddList).setOnClickListener {
                    val listName: String = findViewById<EditText>(R.id.etTaskListNameInput).text.toString()

                    if (listName.isNotEmpty()) {
                        if (context is TaskListActivity ) {
                            (context as TaskListActivity).createTaskList(listName)
                            findViewById<ConstraintLayout>(R.id.clTaskListAddition).visibility = View.GONE
                            findViewById<ConstraintLayout>(R.id.clCardListAddition).visibility = View.VISIBLE
                        }
                    }
                    else {
                        Toast.makeText(
                            context,
                            resources.getString(R.string.toast_task_list_empty_name),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                findViewById<ImageButton>(R.id.ibEditTaskListName).setOnClickListener {
                    findViewById<EditText>(R.id.etTaskListNameChangeInput).setText(model.title)
                    findViewById<ConstraintLayout>(R.id.clTaskListName).visibility = View.GONE
                    findViewById<ConstraintLayout>(R.id.clEditTaskList).visibility = View.VISIBLE
                }

                findViewById<ImageButton>(R.id.ibDeleteTaskList).setOnClickListener {
                    confirmDeletingList(position, model.title)
                }

                findViewById<ImageButton>(R.id.ibEditTaskListName).setOnClickListener {
                    findViewById<EditText>(R.id.etTaskListNameChangeInput).setText(model.title)
                    findViewById<ConstraintLayout>(R.id.clTaskListName).visibility = View.GONE
                    findViewById<ConstraintLayout>(R.id.clEditTaskList).visibility = View.VISIBLE
                }

                findViewById<ImageButton>(R.id.ibEditChangeClose).setOnClickListener {
                    findViewById<ConstraintLayout>(R.id.clTaskListName).visibility = View.VISIBLE
                    findViewById<ConstraintLayout>(R.id.clEditTaskList).visibility = View.GONE
                }

                findViewById<ImageButton>(R.id.ibEditChangeCheck).setOnClickListener {
                    val listName: String = findViewById<EditText>(R.id.etTaskListNameChangeInput).text.toString()

                    if (listName.isNotEmpty()) {
                        if (context is TaskListActivity ) {
                            (context as TaskListActivity).updateTaskList(position, listName, model)
                        }
                    }
                    else {
                        Toast.makeText(
                            context,
                            resources.getString(R.string.toast_task_list_empty_name),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    findViewById<ConstraintLayout>(R.id.clTaskListName).visibility = View.VISIBLE
                    findViewById<ConstraintLayout>(R.id.clEditTaskList).visibility = View.GONE
                }

                findViewById<TextView>(R.id.tvAddCard).setOnClickListener {
                    findViewById<TextView>(R.id.tvAddCard).visibility = View.GONE
                    findViewById<ConstraintLayout>(R.id.clEditCardList).visibility = View.VISIBLE
                }

                findViewById<ImageButton>(R.id.ibEditCardClose).setOnClickListener {
                    findViewById<TextView>(R.id.tvAddCard).visibility = View.VISIBLE
                    findViewById<ConstraintLayout>(R.id.clEditCardList).visibility = View.GONE
                }

                findViewById<ImageButton>(R.id.ibEditCardCheck).setOnClickListener {
                    val cardName: String = findViewById<EditText>(R.id.etCardNameInput).text.toString()

                    if (cardName.isNotEmpty()) {
                        if (context is TaskListActivity ) {
                            (context as TaskListActivity).addCardToTaskList(position, cardName)
                            findViewById<ConstraintLayout>(R.id.clTaskListAddition).visibility = View.GONE
                            findViewById<ConstraintLayout>(R.id.clCardListAddition).visibility = View.VISIBLE
                        }
                    }
                    else {
                        Toast.makeText(
                            context,
                            resources.getString(R.string.toast_card_empty_name),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                findViewById<RecyclerView>(R.id.rvCardList).visibility = View.VISIBLE
                findViewById<RecyclerView>(R.id.rvCardList).layoutManager= LinearLayoutManager(context)
                findViewById<RecyclerView>(R.id.rvCardList).setHasFixedSize(true)
                findViewById<RecyclerView>(R.id.rvCardList).adapter = CardListItemAdapter(context, model.cards)

            }
        }
    }

    private fun confirmDeletingList(position: Int, title: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Once $title is deleted, it can't be recovered.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            if (context is TaskListActivity)
                context.deleteTaskList(position)
        }

        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    public class TaskViewHolder(view: View): RecyclerView.ViewHolder(view)

    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}