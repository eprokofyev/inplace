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
import com.inplace.chat.DateParser
import com.inplace.models.SuperChat
import de.hdodenhof.circleimageview.CircleImageView


class ChatsRecyclerViewAdapter(
        private val context: SwitcherInterface
) : PagingDataAdapter<SuperChat, ViewHolder>(CHATS_COMPARATOR) {

    private var chats: MutableList<SuperChat> = mutableListOf<SuperChat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)


        return MyHolder(view)
    }

    fun setChats(ch: MutableList<SuperChat>) {
        chats = ch
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        Log.d("holder", "here")

        if (holder is MyHolder) {

            val chat = getItem(position) as SuperChat

            holder.name.text = chat.title

            holder.message.text = chat.lastMessage.text

            holder.time.text = DateParser.convertTimeToString(chat.lastMessage.date)

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
        private val CHATS_COMPARATOR = object : DiffUtil.ItemCallback<SuperChat>() {
            override fun areItemsTheSame(oldItem: SuperChat, newItem: SuperChat) =
                oldItem.lastMessage.chatID == newItem.lastMessage.chatID && oldItem.lastMessage.fromMessenger == newItem.lastMessage.fromMessenger

            override fun areContentsTheSame(oldItem: SuperChat, newItem: SuperChat): Boolean =
                oldItem == newItem
        }
    }

}