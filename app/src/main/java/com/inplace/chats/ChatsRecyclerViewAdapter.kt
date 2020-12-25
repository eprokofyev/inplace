package com.inplace.chats

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.inplace.R
import com.inplace.api.ApiImageLoader
import com.inplace.chat.DateParser
import com.inplace.models.Source
import com.inplace.models.SuperChat
import com.inplace.services.ExecutorServices
import de.hdodenhof.circleimageview.CircleImageView


class ChatsRecyclerViewAdapter(
    private val switcher: SwitcherInterface,
    private val context: Activity?
) : PagingDataAdapter<SuperChat, ViewHolder>(CHATS_COMPARATOR) {

    //private val map: HashMap<String, Int> = hashMapOf<String, Int>()

    private val loader = ApiImageLoader.getInstance(context)

    private val executor = ExecutorServices.getInstanceDB()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_chats_item, parent, false)


        return MyHolder(view)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is MyHolder) {

            val chat = getItem(position) as SuperChat

            holder.name.text = chat.title

            /*
            var name = ""


            if (!chat.lastMessage.myMsg && chat.) {
                when (chat.lastMessage.fromMessenger) {
                    Source.VK -> for (ch in chat.vkChats) {
                        val user = ch.sobesedniks.get(chat.lastMessage.userID)
                        if (user != null) {
                            name = user.vk?.name + ": " ?: ""
                            break
                        }
                    }
                    Source.TELEGRAM -> for (ch in chat.telegramChats) {
                        val user = ch.sobesedniks.get(chat.lastMessage.userID)
                        if (user != null) {
                            name = user.telegram?.name + ": " ?: ""
                            break
                        }
                    }
                }
            }
            */

            holder.message.text = chat.lastMessage.text


            holder.mesenger.setImageResource(
                when (chat.lastMessage.fromMessenger) {
                    Source.VK -> R.drawable.ic_vk_logo_no_background
                    Source.TELEGRAM -> R.drawable.ic_tg_logo_no_background
                }
            )




            if (chat.lastMessage.text.isEmpty()) {
                holder.message.text = context?.resources?.getString(R.string.photo) ?: ""
                holder.message.setTextColor(Color.BLUE)
            } else {
                holder.message.setTextColor(Color.GRAY)
            }

            holder.time.text = DateParser.convertTimeToString(chat.lastMessage.date * 1000)


            if (chat.avatarURL.isEmpty()) {
                context?.runOnUiThread {
                    holder.avatar.setImageResource(R.drawable.foto)
                }
            } else {
                executor.execute {
                    loader.getImageByUrl(chat.avatarURL)?.let {
                        context?.runOnUiThread {
                            holder.avatar.setImageBitmap(it)
                        }
                    }
                }
            }


            holder.line.setOnClickListener { v ->

                switcher.switch(chat)

            }
        }
    }

    interface SetterAvartar {
        fun setAvatar(bitmap: Bitmap)
    }

    override fun getItemViewType(position: Int) = 1

    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        var line = view
        var name: TextView = view.findViewById(R.id.name)
        var message: TextView = view.findViewById(R.id.message)
        var time: TextView = view.findViewById(R.id.time)
        var avatar: CircleImageView = view.findViewById(R.id.profile_image)
        var mesenger: ImageView = view.findViewById(R.id.mesenger)
        var photo: ImageView = view.findViewById(R.id.photo)

    }

    companion object {
        private val CHATS_COMPARATOR = object : DiffUtil.ItemCallback<SuperChat>() {
            override fun areItemsTheSame(oldItem: SuperChat, newItem: SuperChat) = oldItem == newItem

            override fun areContentsTheSame(oldItem: SuperChat, newItem: SuperChat) =
                oldItem.lastMessage.chatID == newItem.lastMessage.chatID &&
                        oldItem.lastMessage.fromMessenger == newItem.lastMessage.fromMessenger

        }
    }

}