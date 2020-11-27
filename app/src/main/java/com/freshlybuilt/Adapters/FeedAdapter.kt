package com.freshlybuilt.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.freshlybuilt.Modals.FeedItem
import com.freshlybuilt.R
import kotlinx.android.synthetic.main.feed_recycler_view_item.view.*

class FeedAdapter(private val dataList : List<FeedItem>) : RecyclerView.Adapter<FeedAdapter.FeedAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedAdapterViewHolder {
        val viewInflator = LayoutInflater.from(parent.context).inflate(R.layout.feed_recycler_view_item,parent,false)
        return FeedAdapterViewHolder(viewInflator)
    }



    override fun onBindViewHolder(holder: FeedAdapterViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.image.setImageResource(currentItem.image)
        holder.author.setText(currentItem.author)
        holder.date.setText(currentItem.date)
        holder.description.setText(currentItem.description)
        holder.title.setText(currentItem.title)
    }

    override fun getItemCount() = dataList.size

    class FeedAdapterViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val image : ImageView = itemView.post_list_image
        val title : TextView = itemView.post_list_title
        val author : TextView = itemView.post_list_author
        val date : TextView = itemView.post_list_date
        val description : TextView = itemView.post_list_description
    }
}