package com.inplace.chat

import android.util.Log
import androidx.paging.PagingSource
import com.inplace.api.vk.ApiVK
import com.inplace.models.Message
import com.inplace.models.Source

private const val CHAT_STARTING_PAGE_INDEX = 0

class ChatPagingSource(
    private val conversationId: Int
) : PagingSource<Int, Message>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        val position = params.key ?: CHAT_STARTING_PAGE_INDEX
        val response = ApiVK.getMessages(conversationId, position, position + params.loadSize)

        Log.d("pagingSource", Thread.currentThread().name)
        return if (response.error != null) {
            LoadResult.Error(response.error)
        } else {
            val messages = response.result as List<com.inplace.api.Message>
            LoadResult.Page(
                data = transform(messages),
                prevKey = null,
                nextKey = if (messages.isEmpty()) null else position + 20
            )
        }
    }

    private fun transform(plains: List<com.inplace.api.Message>): List<Message> {
        val result: MutableList<Message> = ArrayList()
        plains.forEach {
            val message = map(it)
            result.add(message)
        }
        return result
    }

    private fun map(messagePlain: com.inplace.api.Message): Message {

        //TODO change to com.inplace.models.Source after api fix
        lateinit var messageSource: Source
        if (messagePlain.fromMessenger == 1)
            messageSource = Source.VK
        else if (messagePlain.fromMessenger == 2)
            messageSource = Source.TELEGRAM

        return Message(
            messagePlain.messageId,
            messagePlain.date * 1000L,
            messagePlain.text,
            messagePlain.fromId,
            messagePlain.myMsg,
            messageSource
        )
    }
}