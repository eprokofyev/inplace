package com.inplace.chat

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.inplace.R
import com.inplace.api.ApiImageLoader
import de.hdodenhof.circleimageview.CircleImageView

class PickedImagesAdapter(
    private val context: Context?,
    private val removeListener: OnImageRemoveClickListener
) : RecyclerView.Adapter<PickedImagesAdapter.PickedImageViewHolder>() {

    private var imageUris: ArrayList<Uri> = ArrayList()

    fun setData(imageUris: ArrayList<Uri>) {
        this.imageUris = imageUris
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickedImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.chat_picked_image_item, parent, false)
        return PickedImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PickedImageViewHolder, position: Int) {
        val model = imageUris[position]
        holder.bind(model)
    }

    override fun getItemCount() = imageUris.size


    inner class PickedImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.pickedImage)
        private val removeButton: CircleImageView = itemView.findViewById(R.id.removeImage_button)

        fun bind(model: Uri) {

            image.setImageBitmap(ApiImageLoader.getInstance(context).getImageByUrl(model.toString()))
            removeButton.setOnClickListener {
                val removedItemIndex = imageUris.indexOf(model)
                imageUris.remove(model)
                notifyItemRemoved(removedItemIndex)
                removeListener.removeImage(model)
            }
        }
    }

}


