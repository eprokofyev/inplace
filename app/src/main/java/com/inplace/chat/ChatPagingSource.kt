package com.inplace.chat


import androidx.paging.PagingSource
import com.inplace.models.Message
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val CHAT_STARTING_PAGE_INDEX = 0

class ChatPagingSource(
    private val chatRepo: ChatRepo,
    private val conversationId: Int
) : PagingSource<Int, Message>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        val position = params.key ?: CHAT_STARTING_PAGE_INDEX

        return suspendCoroutine { continuation ->
            var resumed = false
            chatRepo.getMessages(
                conversationId = conversationId,
                start = position,
                end = position + params.loadSize,
                callback = {
                    if (!resumed) {
                        continuation.resume(
                            if (it is ChatRepoResult.Success) {
                                LoadResult.Page(
                                    data = it.data,
                                    prevKey = null,
                                    nextKey = if (it.data.isEmpty()) null else position + 20
                                )
                            } else {
                                LoadResult.Error((it as ChatRepoResult.Error).error)
                            }
                        )
                        resumed = true
                    }
                })
        }
    }
}