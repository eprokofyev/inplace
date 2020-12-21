package com.inplace.chat

import android.content.Context
import androidx.paging.PagingSource
import com.inplace.chats.ChatsRepo
import com.inplace.chats.ResultVKChat
import com.inplace.models.Source
import com.inplace.models.SuperChat
import com.inplace.models.VKChat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val CHAT_STARTING_PAGE_INDEX = 0

class ChatsPagingDataSource(
    val repo: ChatsRepo,
    val context: Context
) : PagingSource<Int, SuperChat>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SuperChat> {
        val position = params.key ?: CHAT_STARTING_PAGE_INDEX

        return suspendCoroutine { continuation ->
            var resumed = false

            repo.refresh(position, position + 12, object : ChatsRepo.OnRequestCompleteListener {
                override fun onRequestComplete(result: ResultVKChat) {
                    if (!resumed) {
                        continuation.resume(
                            if (result is ResultVKChat.Success) {
                                LoadResult.Page(
                                    data = result.data.map { SuperChat(
                                        it.title,
                                        it.avatarUrl,
                                        it.lastMessage,
                                        true,
                                        arrayListOf( it),
                                        arrayListOf(),
                                        it.chatID,
                                        it.chatID
                                    ) },
                                    prevKey = null,
                                    nextKey = position + 12
                                )
                            } else {
                                LoadResult.Error(Exception("bad result"))
                            }
                        )
                        resumed = true
                    }
                }
            })
        }
    }
}