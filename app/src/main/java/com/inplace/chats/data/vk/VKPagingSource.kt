package com.inplace.chats.data.vk

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import com.inplace.chats.ChatsRepo
import androidx.paging.PagingSource

import com.inplace.chats.ResultVKChat
import com.inplace.chats.data.vk.VKRepository.Companion.DEFAULT_PAGE_INDEX
import com.inplace.chats.data.vk.VKRepository.Companion.DEFAULT_PAGE_SIZE
import com.inplace.models.SuperChat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ExperimentalPagingApi
class VKPagingSource(
    val repo: ChatsRepo,
) : PagingSource<Int, SuperChat>() {


    override suspend fun load(params: LoadParams<Int>): PagingSource.LoadResult<Int, SuperChat> {
        val position = params.key ?: 0

        return suspendCoroutine { continuation ->
            var resumed = false

            repo.refresh(position, position + DEFAULT_PAGE_SIZE, object : ChatsRepo.OnRequestCompleteListener {
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
                                        hashMapOf(it.chatID to it),
                                        hashMapOf(),
                                        it.chatID,
                                        it.chatID
                                    ) },
                                    prevKey = if (position == DEFAULT_PAGE_SIZE) null else position - DEFAULT_PAGE_SIZE,
                                    nextKey = position + DEFAULT_PAGE_SIZE
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