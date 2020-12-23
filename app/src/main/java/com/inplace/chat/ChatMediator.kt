package com.inplace.chat

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.inplace.chat.db.ChatRemoteKey
import com.inplace.db.AppDatabase
import com.inplace.models.Message
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ExperimentalPagingApi
class ChatMediator(
    private val chatID: Long,
    private val chatRepo: ChatRepo,
    private val database: AppDatabase,
) : RemoteMediator<Int, Message>() {

    private val messageDao = database.getMessageDao()
    private val chatRemoteKeyDao = database.getChatRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Message>
    ): MediatorResult {
        return try {
            var loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    Log.d("mediator","LoadType.REFRESH")
                    null}

                LoadType.PREPEND -> {
                    Log.d("mediator","LoadType.PREPEND")
                    return MediatorResult.Success(
                    endOfPaginationReached = true
                )}

                LoadType.APPEND -> {
                    Log.d("mediator","LoadType.APPEND")

                    val remoteKey = database.withTransaction {
                        chatRemoteKeyDao.remoteKeyByChatId(chatID)
                    }

                    if (remoteKey.nextKey == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }

                    remoteKey.nextKey
                }
            }

            if (loadKey == null) {
                loadKey = 0
            }

            val response: ChatRepoResult = suspendCoroutine { continuation ->
                var resumed = false
                chatRepo.getMessages(
                    conversationId = chatID.toInt(),
                    start = loadKey,
                    end = loadKey + state.config.pageSize,
                    callback = {
                        if (!resumed) {
                            continuation.resume(it)
                            resumed = true
                        }
                    })
            }

            val result = if (response is ChatRepoResult.Success) {
                response.data
            } else {
                throw Error((response as ChatRepoResult.Error).error)
            }

            val isEndOfList = result.isEmpty()

            val nextKey = if (isEndOfList) null else loadKey + 20

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    chatRemoteKeyDao.deleteByChatId(chatID)
                    messageDao.deleteByChatId(chatID)
                }

                chatRemoteKeyDao.insertOrReplace(
                    ChatRemoteKey(chatID, nextKey)
                )

                messageDao.insertAll(result)
            }

            MediatorResult.Success(
                endOfPaginationReached = nextKey == null
            )
        } catch (e: Error) {
            MediatorResult.Error(e)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        }
    }
}
