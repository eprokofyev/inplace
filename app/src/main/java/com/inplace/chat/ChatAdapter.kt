package com.inplace.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inplace.R
import com.inplace.models.Source
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(var messagesList: List<ListObject>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setMessages(messages: MutableList<ListObject>) {
        this.messagesList = messages
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return messagesList.get(position).getType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        lateinit var viewHolder: RecyclerView.ViewHolder
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        when (viewType) {
            MessageType.DATE -> {
                val dateView = inflater.inflate(R.layout.chat_date_item, parent, false)
                viewHolder = DateViewHolder(dateView)
            }
            MessageType.HOST -> {
                val hostMessageView =
                    inflater.inflate(R.layout.chat_host_message_item, parent, false)
                viewHolder = HostMessageViewHolder(hostMessageView)
            }
            MessageType.TARGET -> {
                val targetMessageVIew =
                    inflater.inflate(R.layout.chat_target_message_item, parent, false)
                viewHolder = TargetMessageViewHolder(targetMessageVIew)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            MessageType.DATE -> {
                val model = messagesList[position] as DateObject
                val dateViewHolder = holder as DateViewHolder
                dateViewHolder.dateTextView.text = model.date
            }
            MessageType.HOST -> {
                val model = messagesList[position] as MessagesObject
                val hostMessageHolder = holder as HostMessageViewHolder
                hostMessageHolder.messageText.text = model.message.text
                hostMessageHolder.sentTime.text = DateParser.convertTimeToString(model.message.date)
                when (model.message.fromMessenger) {
                    Source.TELEGRAM -> {
                        hostMessageHolder.sourceTG.visibility = View.VISIBLE
                        hostMessageHolder.sourceVK.visibility = View.GONE
                    }
                    Source.VK -> {
                        hostMessageHolder.sourceTG.visibility = View.GONE
                        hostMessageHolder.sourceVK.visibility = View.VISIBLE
                    }
                }
            }
            MessageType.TARGET -> {
                val model = messagesList[position] as MessagesObject
                val targetMessageHolder = holder as TargetMessageViewHolder
                targetMessageHolder.messageText.text = model.message.text
                targetMessageHolder.sentTime.text =
                    DateParser.convertTimeToString(model.message.date)
                targetMessageHolder.messageSender.visibility = View.VISIBLE
                targetMessageHolder.messageSender.text = "Username"
                targetMessageHolder.messageSenderAvatar.visibility = View.VISIBLE
                when (model.message.fromMessenger) {
                    Source.TELEGRAM -> {
                        targetMessageHolder.sourceTG.visibility = View.VISIBLE
                        targetMessageHolder.sourceVK.visibility = View.GONE
                    }
                    Source.VK -> {
                        targetMessageHolder.sourceTG.visibility = View.GONE
                        targetMessageHolder.sourceVK.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    inner class HostMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val sentTime: TextView = itemView.findViewById(R.id.sentTime)
        val sourceTG: ImageView = itemView.findViewById(R.id.sourceTG)
        val sourceVK: ImageView = itemView.findViewById(R.id.sourceVK)
    }

    inner class TargetMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val sentTime: TextView = itemView.findViewById(R.id.sentTime)
        val sourceTG: ImageView = itemView.findViewById(R.id.sourceTG)
        val sourceVK: ImageView = itemView.findViewById(R.id.sourceVK)
        val messageSender: TextView = itemView.findViewById(R.id.messageSender)
        var messageSenderAvatar: CircleImageView = itemView.findViewById(R.id.messageSenderAvatar)
    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.date_textView)
    }


}