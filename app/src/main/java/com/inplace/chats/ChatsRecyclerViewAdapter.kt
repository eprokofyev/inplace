package com.inplace.chats

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inplace.R
import com.inplace.models.Chat
import de.hdodenhof.circleimageview.CircleImageView


class ChatsRecyclerViewAdapter(
        private val context: SwitcherInterface
) : RecyclerView.Adapter<ChatsRecyclerViewAdapter.ViewHolder>() {

    private var chats: MutableList<Chat> = mutableListOf<Chat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    fun setChats(ch: MutableList<Chat>) {
        chats = ch
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.name.text = chats[position].sobesedniks.firstOrNull()?.name ?: "default"

        holder.message.text = chats[position].messages.firstOrNull()?.text ?: "defaul"

        holder.time.text = chats[position].messages.firstOrNull()?.date.toString() ?: "defaul"


        if (chats[position].avatar != null) {
            holder.avatar.setImageBitmap(chats[position].avatar)
        }

        holder.line.setOnClickListener { v ->

            context.switch(chats[position])

        }
    }

    override fun getItemCount(): Int = chats.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val line = view
        val name: TextView = view.findViewById(R.id.name)
        val message: TextView = view.findViewById(R.id.message)
        val time: TextView = view.findViewById(R.id.time)
        val avatar: CircleImageView = view.findViewById(R.id.profile_image)
    }
}