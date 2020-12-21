package com.inplace.chat

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.inplace.R
import com.inplace.api.ApiImageLoader

class MessagePhotosAdapter(private val context: Context,private val photos: ArrayList<String>) : RecyclerView.Adapter<MessagePhotosAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PhotoViewHolder(inflater.inflate(R.layout.chat_message_image_item,parent,false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val model = photos[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val image:ImageView = itemView.findViewById(R.id.messageImage)

        fun bind(uri:String){
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()
            Glide
                .with(context)
                .load(uri)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.error_load_image)
                .into(image)
        }
    }
}