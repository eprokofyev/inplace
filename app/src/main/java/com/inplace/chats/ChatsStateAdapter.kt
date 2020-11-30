package com.inplace.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inplace.R

class ChatsStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<ChatsStateAdapter.LoadStateViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_load_state_layout, parent, false)
        return LoadStateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        val errorMessage = holder.errorMessage
        val progressBar = holder.progressBar
        val retryButton = holder.retryButton

        errorMessage.isVisible = loadState !is LoadState.Loading
        progressBar.isVisible = loadState !is LoadState.Loading
        retryButton.isVisible = loadState !is LoadState.Loading

        if (loadState is LoadState.Error) {
            errorMessage.text = loadState.error.localizedMessage
        }

        retryButton.setOnClickListener {
            retry.invoke()
        }
    }

    inner class LoadStateViewHolder(private val itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val errorMessage: TextView = itemView.findViewById(R.id.load_state_errorMessage)
        val progressBar: ProgressBar = itemView.findViewById(R.id.load_state_progress)
        val retryButton: Button = itemView.findViewById(R.id.load_state_retry)
    }

}