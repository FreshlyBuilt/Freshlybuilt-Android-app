package com.freshlybuilt.Adapters

import android.app.Activity
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freshlybuilt.Modals.FeedItem
import com.freshlybuilt.R
import kotlinx.android.synthetic.main.feed_recycler_view_item.view.*
import java.lang.Exception
import java.lang.System.load

class FeedAdapter(private val dataList : List<FeedItem>) : RecyclerView.Adapter<FeedAdapter.FeedAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedAdapterViewHolder {
        val viewInflator = LayoutInflater.from(parent.context).inflate(R.layout.feed_recycler_view_item,parent,false)
        return FeedAdapterViewHolder(viewInflator)
    }

    override fun onBindViewHolder(holder: FeedAdapterViewHolder, position: Int) {
        val currentItem = dataList[position]
        try{
            if (currentItem.imageUrl == "default") {
                holder.image.setImageResource(currentItem.image)
            }else{
                Glide.with(holder.image.context).load(currentItem.imageUrl).into(holder.image)
            }

        }catch(e : Exception){

        }


        holder.author.setText(HtmlCompat.fromHtml(currentItem.author,HtmlCompat.FROM_HTML_MODE_LEGACY))
        holder.date.setText(currentItem.date)
        holder.description.setText(HtmlCompat.fromHtml(currentItem.description,HtmlCompat.FROM_HTML_MODE_LEGACY))
        holder.title.setText(HtmlCompat.fromHtml(currentItem.title,HtmlCompat.FROM_HTML_MODE_LEGACY))
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