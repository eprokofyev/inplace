package com.inplace.chat

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import com.inplace.api.vk.ApiVK
import com.inplace.chats.ChatsRepo
import com.inplace.chats.RepoResult
import com.inplace.models.Chat
import com.inplace.models.Source
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val CHAT_STARTING_PAGE_INDEX = 0

class ChatsPagingDataSource(
    val repo: ChatsRepo,
    val context: Context
) : PagingSource<Int, Chat>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Chat> {
        val position = params.key ?: CHAT_STARTING_PAGE_INDEX

        return suspendCoroutine { continuation ->
            var resumed = false

            repo.refresh(context, position, position + 12, object : ChatsRepo.OnRequestCompleteListener {
                override fun onRequestComplete(result: RepoResult) {
                    if (!resumed) {
                        continuation.resume(
                            if (result is RepoResult.Success) {
                                LoadResult.Page(
                                    data = result.data,
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