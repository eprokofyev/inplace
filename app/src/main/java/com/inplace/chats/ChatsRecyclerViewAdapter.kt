package com.inplace.chats

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.inplace.R
import com.inplace.chat.ChatModel
import com.inplace.chat.DateParser
import com.inplace.models.Chat
import de.hdodenhof.circleimageview.CircleImageView


class ChatsRecyclerViewAdapter(
        private val context: SwitcherInterface
) : PagingDataAdapter<Chat, ViewHolder>(CHATS_COMPARATOR) {

    private var chats: MutableList<Chat> = mutableListOf<Chat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)


        return MyHolder(view)
    }

    fun setChats(ch: MutableList<Chat>) {
        chats = ch
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        Log.d("holder", "here")

        if (holder is MyHolder) {

            val chat = getItem(position) as Chat

            holder.name.text = chat.title

            holder.message.text = chat.messages.firstOrNull()?.text ?: "defaul"

            holder.time.text = DateParser.convertTimeToString(chat.messages.firstOrNull()?.date ?: 0)

            if (chat.avatar != null) {
                holder.avatar.setImageBitmap(chat.avatar)
            }

            holder.line.setOnClickListener { v ->

                context.switch(chat)

            }
        }
    }

    override fun getItemViewType(position: Int) = 1

    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val line = view
        val name: TextView = view.findViewById(R.id.name)
        val message: TextView = view.findViewById(R.id.message)
        val time: TextView = view.findViewById(R.id.time)
        val avatar: CircleImageView = view.findViewById(R.id.profile_image)

    }

    companion object {
        private val CHATS_COMPARATOR = object : DiffUtil.ItemCallback<Chat>() {
            override fun areItemsTheSame(oldItem: Chat, newItem: Chat) = oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean =
                oldItem == newItem
        }
    }

}