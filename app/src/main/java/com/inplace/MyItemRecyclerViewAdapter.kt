package com.inplace

import android.graphics.Color.BLUE
import android.graphics.Color.RED
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView


class MyItemRecyclerViewAdapter(
    private val values: MutableList<Int>,
    private val context: SwitcherInterface
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = "name" + values[position].toString()
        holder.name.setTextColor(
            if (values[position].rem(2)  == 0) {
                RED
            } else {
                BLUE
            })
        holder.message.text = "message" + values[position].toString()

        holder.time.text = "17:00"

        holder.line.setOnClickListener { v ->

            context.switch(10, holder.name.currentTextColor)

        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val line = view
        val name: TextView = view.findViewById(R.id.name)
        val message: TextView = view.findViewById(R.id.message)
        val time: TextView = view.findViewById(R.id.time)
        val avatar: CircleImageView = view.findViewById(R.id.profile_image)
    }
}