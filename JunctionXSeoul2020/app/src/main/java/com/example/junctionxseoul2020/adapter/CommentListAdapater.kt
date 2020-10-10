package com.example.junctionxseoul2020.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.junctionxseoul2020.R
import java.util.*

class CommentListAdapater(var comments: ArrayList<String>) :
    RecyclerView.Adapter<CommentListAdapater.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var commentTextView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentListAdapater.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.comment_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CommentListAdapater.ViewHolder, position: Int) {
        holder.commentTextView.text = comments[position]
    }

    override fun getItemCount(): Int {
        return comments.size
    }


}