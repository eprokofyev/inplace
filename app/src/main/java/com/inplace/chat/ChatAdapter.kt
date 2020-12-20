package com.inplace.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.inplace.R
import com.inplace.models.Source
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter : PagingDataAdapter<ChatModel, RecyclerView.ViewHolder>(MESSAGE_COMPARATOR) {

    private val viewPool = RecyclerView.RecycledViewPool()

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
                val targetMessageView =
                    inflater.inflate(R.layout.chat_target_message_item, parent, false)
                viewHolder = TargetMessageViewHolder(targetMessageView)
            }
            MessageType.DATE -> {
                val dateItemView = inflater.inflate(R.layout.chat_date_item, parent, false)
                viewHolder = DateViewHolder(dateItemView)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            MessageType.HOST -> {
                val model = getItem(position) as ChatModel.MessageItem
                val hostMessageHolder = holder as HostMessageViewHolder

                hostMessageHolder.bind(model)
            }
            MessageType.TARGET -> {
                val model = getItem(position) as ChatModel.MessageItem
                val targetMessageHolder = holder as TargetMessageViewHolder

                targetMessageHolder.bind(model)
            }
            MessageType.DATE -> {
                val model = getItem(position) as ChatModel.DateItem
                val dateHolder = holder as DateViewHolder

                dateHolder.bind(model)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item is ChatModel.MessageItem && item.message.myMsg)
            MessageType.HOST
        else if (item is ChatModel.MessageItem && !item.message.myMsg)
            MessageType.TARGET
        else
            MessageType.DATE
    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val date: TextView = itemView.findViewById(R.id.date_textView)

        fun bind(model: ChatModel.DateItem) {
            date.text = model.date
        }
    }


    inner class HostMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        private val sentTime: TextView = itemView.findViewById(R.id.sentTime)
        private val sourceTG: ImageView = itemView.findViewById(R.id.sourceTG)
        private val sourceVK: ImageView = itemView.findViewById(R.id.sourceVK)
        private val photosRecyclerView: RecyclerView = itemView.findViewById(R.id.messagePhotos)

        fun bind(model: ChatModel.MessageItem) {
            messageText.text = model.message.text
            sentTime.text = DateParser.convertTimeToString(model.message.date)

            val photos = model.message.photos

            if (photos.isNotEmpty()) {
                val spanCount = when (photos.size) {
                    1 -> 1
                    in 2..4 -> 2
                    else -> 3
                }
                val photosLayoutManager =
                    GridLayoutManager(photosRecyclerView.context,spanCount)
                photosRecyclerView.apply {
                    layoutManager = photosLayoutManager
                    adapter = MessagePhotosAdapter(context,photos)
                    setRecycledViewPool(viewPool)
                    isVisible = true
                }
            }else{
                if(photosRecyclerView.isVisible){
                    photosRecyclerView.adapter = null
                    photosRecyclerView.isVisible = false
                }
            }

            when (model.message.fromMessenger) {
                Source.VK -> {
                    sourceTG.isVisible = false
                    sourceVK.isVisible = true
                }
                Source.TELEGRAM -> {
                    sourceTG.visibility = View.VISIBLE
                    sourceVK.visibility = View.GONE
                }
            }
        }


    }

    inner class TargetMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        private val sentTime: TextView = itemView.findViewById(R.id.sentTime)
        private val sourceTG: ImageView = itemView.findViewById(R.id.sourceTG)
        private val sourceVK: ImageView = itemView.findViewById(R.id.sourceVK)
        private val messageSender: TextView = itemView.findViewById(R.id.messageSender)
        private val messageSenderAvatar: CircleImageView =
            itemView.findViewById(R.id.messageSenderAvatar)
        private val photosRecyclerView:RecyclerView = itemView.findViewById(R.id.messagePhotos)

        fun bind(model: ChatModel.MessageItem) {
            messageText.text = model.message.text
            sentTime.text = DateParser.convertTimeToString(model.message.date)

            val photos = model.message.photos

            if (photos.size != 0) {
                val spanCount = when (photos.size) {
                    1 -> 1
                    in 2..4 -> 2
                    else -> 3
                }
                val photosLayoutManager =
                    GridLayoutManager(photosRecyclerView.context,spanCount)

                photosRecyclerView.apply {
                    layoutManager = photosLayoutManager
                    adapter = MessagePhotosAdapter(context,photos)
                    setRecycledViewPool(viewPool)
                    isVisible = true
                }
            }else{
                if(photosRecyclerView.isVisible){
                    photosRecyclerView.adapter = null
                    photosRecyclerView.isVisible = false
                }
            }

            when (model.message.fromMessenger) {
                Source.VK -> {
                    sourceTG.visibility = View.GONE
                    sourceVK.visibility = View.VISIBLE
                }
                Source.TELEGRAM -> {
                    sourceTG.visibility = View.VISIBLE
                    sourceVK.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        private val MESSAGE_COMPARATOR = object : DiffUtil.ItemCallback<ChatModel>() {
            override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
                return (oldItem is ChatModel.MessageItem && newItem is ChatModel.MessageItem &&
                        oldItem.message.messageID == newItem.message.messageID) ||
                        (oldItem is ChatModel.DateItem && newItem is ChatModel.DateItem &&
                                oldItem.date == newItem.date)
            }

            override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean =
                oldItem == newItem
        }
    }
}