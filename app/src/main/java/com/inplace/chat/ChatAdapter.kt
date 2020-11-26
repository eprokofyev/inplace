package com.inplace.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inplace.R
import com.inplace.models.Message
import com.inplace.models.Source
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ChatAdapter(var messagesList: LinkedList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var beforAddPosition = 0

    fun setMessages(messages: List<Message>) {
        beforAddPosition = messagesList.size
        messages.forEach {
            messagesList.add(it)
        }
        notifyItemRangeInserted(beforAddPosition, messages.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        lateinit var viewHolder: RecyclerView.ViewHolder
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        when (viewType) {
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
            MessageType.HOST -> {
                val model = messagesList[position]
                val hostMessageHolder = holder as HostMessageViewHolder
                val prevItem = if (position == 0) null else messagesList[position - 1]
                hostMessageHolder.bind(model, prevItem)
            }
            MessageType.TARGET -> {
                val model = messagesList[position]
                val targetMessageHolder = holder as TargetMessageViewHolder
                val prevItem = if (position == 0) null else messagesList[position - 1]
                targetMessageHolder.bind(model, prevItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messagesList[position].myMsg) MessageType.HOST else MessageType.TARGET
    }

    fun addMessage(message: Message) {
        messagesList.addFirst(message)
        notifyDataSetChanged()
    }

    inner class HostMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: TextView = itemView.findViewById(R.id.messageText)
        var sentTime: TextView = itemView.findViewById(R.id.sentTime)
        var sourceTG: ImageView = itemView.findViewById(R.id.sourceTG)
        var sourceVK: ImageView = itemView.findViewById(R.id.sourceVK)
        var dateTextView: TextView = itemView.findViewById(R.id.date_textView)
        var newMessage: TextView = itemView.findViewById(R.id.newMessage_textView)

        fun bind(model: Message, prevItem: Message? = null) {
            messageText.text = model.text
            sentTime.text = DateParser.convertTimeToString(model.date)
            when (model.fromMessenger) {
                Source.VK -> {
                    sourceTG.visibility = View.GONE
                    sourceVK.visibility = View.VISIBLE
                }
                Source.TELEGRAM -> {
                    sourceTG.visibility = View.VISIBLE
                    sourceVK.visibility = View.GONE
                }
            }
            if (prevItem != null) {
                val prevDate = DateParser.getDateAsUnix(prevItem.date)
                val currDate = DateParser.getDateAsUnix(model.date)
                if (currDate != prevDate) {
                    dateTextView.visibility = View.VISIBLE
                    dateTextView.text = DateParser.convertDateToString(prevDate)
                }
            }
        }
    }

    inner class TargetMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val sentTime: TextView = itemView.findViewById(R.id.sentTime)
        val sourceTG: ImageView = itemView.findViewById(R.id.sourceTG)
        val sourceVK: ImageView = itemView.findViewById(R.id.sourceVK)
        val messageSender: TextView = itemView.findViewById(R.id.messageSender)
        var messageSenderAvatar: CircleImageView = itemView.findViewById(R.id.messageSenderAvatar)
        val dateTextView: TextView = itemView.findViewById(R.id.date_textView)
        val newMessage: TextView = itemView.findViewById(R.id.newMessage_textView)

        fun bind(model: Message, prevItem: Message? = null) {
            messageText.text = model.text
            sentTime.text = DateParser.convertTimeToString(model.date)
            when (model.fromMessenger) {
                Source.VK -> {
                    sourceTG.visibility = View.GONE
                    sourceVK.visibility = View.VISIBLE
                }
                Source.TELEGRAM -> {
                    sourceTG.visibility = View.VISIBLE
                    sourceVK.visibility = View.GONE
                }
            }
            if (prevItem != null) {
                val prevDate = DateParser.getDateAsUnix(prevItem.date)
                val currDate = DateParser.getDateAsUnix(model.date)
                if (currDate != prevDate) {
                    dateTextView.visibility = View.VISIBLE
                    dateTextView.text = DateParser.convertDateToString(prevDate)
                }
            }
        }
    }
}
