package `in`.walnutlabs.teamup.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.walnutlabs.teamup.R
import `in`.walnutlabs.teamup.models.Card

open class CardListItemAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CardListViewHolder(
            LayoutInflater
                .from(context)
                .inflate(
                    R.layout.item_card,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model: Card = list[position]

        if (holder is CardListViewHolder) {
            with (holder.itemView) {
                findViewById<TextView>(R.id.tvCardName).text = model.name
                findViewById<TextView>(R.id.tvCardCreatorName).text = model.createdBy
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.listener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, card: Card)
    }

    public class CardListViewHolder(view: View): RecyclerView.ViewHolder(view)
}