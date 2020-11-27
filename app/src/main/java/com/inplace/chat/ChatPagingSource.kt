package com.inplace.chat

import androidx.paging.PagingSource
import com.inplace.api.vk.ApiVK
import com.inplace.models.Message
import com.inplace.models.Source
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.random.Random

private const val CHAT_STARTING_PAGE_INDEX = 0

class ChatPagingSource(
        private val conversationId: Int
) : PagingSource<Int, Message>() {

    private val executor: ExecutorService = Executors.newFixedThreadPool(5)


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        val position = params.key ?: CHAT_STARTING_PAGE_INDEX

        val response = ApiVK.getMessages(conversationId, position, position + params.loadSize)
        var messages: ArrayList<com.inplace.api.Message> = arrayListOf()


        if (response.error != null) {
            //todo process error
        } else {
            messages = response.result as ArrayList<com.inplace.api.Message>
        }


        return LoadResult.Page(
                data = transform(messages),
                prevKey = null,
                nextKey = if (messages.isEmpty()) null else position + 20
        )


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
        lateinit var messageSource: Source
        if (messagePlain.fromMessenger == 1)
            messageSource = Source.VK
        else if (messagePlain.fromMessenger == 2)
            messageSource = Source.TELEGRAM

        return Message(
                messagePlain.date * 1000L + Random.nextInt(0, 999),
                messagePlain.text,
                messagePlain.fromId,
                messagePlain.myMsg,
                messageSource
        )
    }
}