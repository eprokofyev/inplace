package com.inplace.chats.data.vk

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import com.inplace.chats.ChatsRepo
import com.inplace.chats.ResultVKChat
import com.inplace.chats.data.vk.VKRepository.Companion.DEFAULT_PAGE_SIZE
import com.inplace.chats.repository.vk.RemoteKeys
import com.inplace.db.AppDatabase
import com.inplace.models.SuperChat
import com.inplace.models.VKChat
import com.vk.api.sdk.VK
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ExperimentalPagingApi
class VKMediator(
    private val database: AppDatabase,
    val repo: ChatsRepo,
) : RemoteMediator<Int, VKChat>() {

    val chatsDao = database.getChatsDao()
    val remoteKeysDao = database.getRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, VKChat>
    ): MediatorResult {

        Log.d("status", "state" + state.toString())
        val pageKeyData = getKeyPageData(loadType, state)
        Log.d("status", "page" + pageKeyData.toString())
        val page = when (pageKeyData) {
            is MediatorResult.Success -> {
                Log.d("status", "page1" + pageKeyData.toString())
                return pageKeyData
            }
            else -> {
                pageKeyData as Int
            }
        }

        // The network load method takes an optional after=<user.id>
        // parameter. For every page after the first, pass the last user
        // ID to let it continue from where it left off. For REFRESH,
        // pass null to load the first page.
        Log.d("status", "anch " + state.anchorPosition.toString())

        /*val loadKey = when (loadType) {
            LoadType.REFRESH -> {
                database.withTransaction {
                    remoteKeysDao.clearRemoteKeys()
                    chatsDao.deleteChats()
                }
                Log.d("status", "Refresh")
                null
            }
            // In this example, you never need to prepend, since REFRESH
            // will always load the first page in the list. Immediately
            // return, reporting end of pagination.
            LoadType.PREPEND -> {
                Log.d("status", "PREPEND")
                //return MediatorResult.Success(endOfPaginationReached = true)
            }

            LoadType.APPEND -> {
                Log.d("status", "APPEPEND")
                //val lastItem = state.lastItemOrNull()

                // You must explicitly check if the last item is null when
                // appending, since passing null to networkService is only
                // valid for initial load. If lastItem is null it means no
                // items were loaded after the initial REFRESH and there are
                // no more items to load.
                //if (lastItem == null) {
                    //return MediatorResult.Success(
                      //  endOfPaginationReached = true
                    //)
                //}

                //state.lastItemOrNull()?.local_id ?: 0
            }
        }
        */

        Log.d("status", page.toString())

        val result: ResultVKChat = suspendCoroutine { continuation ->
            var resumed = false

            repo.refresh(page, page + state.config.pageSize, object : ChatsRepo.OnRequestCompleteListener {
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
                    Log.d("status", "Refresh")
                    remoteKeysDao.clearRemoteKeys()
                    chatsDao.deleteChats()
                }
                val prevKey = if (page == DEFAULT_PAGE_SIZE) null else page - DEFAULT_PAGE_SIZE
                val nextKey = if (result.data.size == 0) null else page + DEFAULT_PAGE_SIZE
                val keys = result.data.map {
                    RemoteKeys(chatID = it.chatID, prevKey = prevKey, nextKey = nextKey)
                }
                print(keys.toString())
                remoteKeysDao.insertAll(keys)
                chatsDao.insertChats(result.data)
            }
        } else {
            return MediatorResult.Success(endOfPaginationReached = false)
        }

        return MediatorResult.Success(endOfPaginationReached = result.data.size == 0)
    }

    suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, VKChat>): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                val t = remoteKeys?.nextKey ?: 0
                Log.d("status", "REFRESH " + t.toString())
                t
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                val t = remoteKeys?.nextKey ?: 0
                Log.d("status", "APPEND " + t.toString())
                t
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                //end of list condition reached
                remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                val t = remoteKeys.prevKey
                Log.d("status", "PREPEND " + t.toString())
                t
            }
        }
    }

    /**
     * get the last remote key inserted which had the data
     */
    private suspend fun getLastRemoteKey(state: PagingState<Int, VKChat>): RemoteKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { database.getRemoteKeysDao().remoteKeysDoggoId(it.chatID) }
    }

    /**
     * get the first remote key inserted which had the data
     */
    private suspend fun getFirstRemoteKey(state: PagingState<Int, VKChat>): RemoteKeys? {
        return state.pages
            .firstOrNull() { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { database.getRemoteKeysDao().remoteKeysDoggoId(it.chatID) }
    }

    /**
     * get the closest remote key inserted which had the data
     */
    private suspend fun getClosestRemoteKey(state: PagingState<Int, VKChat>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.chatID?.let {
                database.getRemoteKeysDao().remoteKeysDoggoId(it)
            }
        }
    }
}