package com.freshlybuilt.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.freshlybuilt.Modals.TagItem
import com.freshlybuilt.R
import kotlinx.android.synthetic.main.tag_recycler_view_item.view.*

class TagAdapter(private val dataList : ArrayList<TagItem>) : RecyclerView.Adapter<TagAdapter.TagAdapterViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagAdapterViewHolder {
        val viewInflate = LayoutInflater.from(parent.context).inflate(R.layout.tag_recycler_view_item,parent,false)
        return TagAdapterViewHolder(viewInflate)
    }

    override fun onBindViewHolder(holder: TagAdapterViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.quesTitle.setText(HtmlCompat.fromHtml(currentItem.quesTitle,HtmlCompat.FROM_HTML_MODE_LEGACY))
        holder.quesContent.setText(HtmlCompat.fromHtml(currentItem.quesContent,HtmlCompat.FROM_HTML_MODE_LEGACY))
        holder.quesDate.setText(HtmlCompat.fromHtml(currentItem.quesDATE,HtmlCompat.FROM_HTML_MODE_LEGACY))
    }

    override fun getItemCount() = dataList.size

    class TagAdapterViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val quesTitle = itemView.tag_item_question
        val quesContent = itemView.tag_item_question_excerpt
        val quesDate = itemView.tag_item_question_date
    }
}