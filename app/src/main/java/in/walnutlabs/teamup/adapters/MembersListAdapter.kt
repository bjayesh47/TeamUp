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
import `in`.walnutlabs.teamup.models.User

open class MembersListAdapter(
    private val context: Context,
    private val membersList: ArrayList<User>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_member,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = membersList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user: User = membersList[position]

        if (holder is MyViewHolder) {
            with (holder.itemView) {
                Glide
                    .with(context)
                    .load(user.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_glide_placeholder)
                    .into(findViewById<CircleImageView>(R.id.civProfilePic))

                findViewById<TextView>(R.id.tvMemberName).text = user.name
                findViewById<TextView>(R.id.tvMemberEmail).text = user.email
            }
        }
    }

    public class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}