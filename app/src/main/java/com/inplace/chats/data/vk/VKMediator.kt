package com.inplace.chats.data.vk

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import com.inplace.chats.ChatsRepo
import com.inplace.chats.ResultVKChat
import com.inplace.db.AppDatabase
import com.inplace.models.SuperChat
import com.inplace.models.VKChat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ExperimentalPagingApi
class VKMediator(
    private val database: AppDatabase,
    val repo: ChatsRepo,
) : RemoteMediator<Int, VKChat>() {

    val chatsDao = database.getChatsDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, VKChat>
    ): MediatorResult {

        // The network load method takes an optional after=<user.id>
        // parameter. For every page after the first, pass the last user
        // ID to let it continue from where it left off. For REFRESH,
        // pass null to load the first page.
        val loadKey = when (loadType) {
            LoadType.REFRESH -> {
                Log.d("status", "Refresh")
                null
            }
            // In this example, you never need to prepend, since REFRESH
            // will always load the first page in the list. Immediately
            // return, reporting end of pagination.
            LoadType.PREPEND -> {
                Log.d("status", "PREPEND")
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            LoadType.APPEND -> {
                Log.d("status", "APPEPEND")
                val lastItem = state.lastItemOrNull()

                // You must explicitly check if the last item is null when
                // appending, since passing null to networkService is only
                // valid for initial load. If lastItem is null it means no
                // items were loaded after the initial REFRESH and there are
                // no more items to load.
                if (lastItem == null) {
                    return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }

                state.lastItemOrNull()?.local_id ?: 0
            }
        }

        Log.d("load", loadKey.toString())

        val result: ResultVKChat = suspendCoroutine { continuation ->
            var resumed = false

            repo.refresh(loadKey ?: 0, loadKey ?: 0 + VKRepository.DEFAULT_PAGE_SIZE, object : ChatsRepo.OnRequestCompleteListener {
                override fun onRequestComplete(result: ResultVKChat) {
                    if (!resumed) {
                        continuation.resume(
                            result
                        )
                        resumed = true
                    }
                }
            })
        }

        if (result is ResultVKChat.Success) {
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    chatsDao.deleteChats()
                }

                // Insert new users into database, which invalidates the
                // current PagingData, allowing Paging to present the updates
                // in the DB.
                chatsDao.insertChats(result.data)
            }
        } else {
            return MediatorResult.Error(Exception("bad result"))
        }

        return MediatorResult.Success(endOfPaginationReached = true)
    }
}