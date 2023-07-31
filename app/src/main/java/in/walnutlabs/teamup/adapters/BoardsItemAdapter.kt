package `in`.walnutlabs.teamup.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import `in`.walnutlabs.teamup.R
import `in`.walnutlabs.teamup.models.Board

open class BoardsItemAdapter(
    private val context: Context,
    private var list: ArrayList<Board>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater
                .from(context)
                .inflate(R.layout.item_board, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model: Board = list[position]
        if (holder is MyViewHolder) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_glide_placeholder)
                .into(holder.itemView.findViewById<CircleImageView>(R.id.civBoardPic))

            holder.itemView.findViewById<TextView>(R.id.tvBoardName).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tvCreateBy).text = model.createdBy

            holder.itemView.setOnClickListener {
                listener?.let {
                    it.onClick(position, model)
                }
            }
        }
    }

    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }

    public class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {}
}