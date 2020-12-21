package com.inplace.chat

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bumptech.glide.load.HttpException
import com.inplace.AppDatabase
import com.inplace.chat.db.ChatRemoteKey
import com.inplace.models.Message
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ExperimentalPagingApi
class ChatRemoteMediator(
    private val chatID: Long,
    private val chatRepo: ChatRepo,
    private val database: AppDatabase,
) : RemoteMediator<Int, Message>() {

    private val messageDao = database.getMessageDao()
    private val chatRemoteKeysDao = database.getChatRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Message>
    ): MediatorResult {
        return try {
            // The network load method takes an optional String
            // parameter. For every page after the first, pass the String
            // token returned from the previous page to let it continue
            // from where it left off. For REFRESH, pass null to load the
            // first page.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 0
                // In this example, you never need to prepend, since REFRESH
                // will always load the first page in the list. Immediately
                // return, reporting end of pagination.
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                // Query remoteKeyDao for the next RemoteKey.
                LoadType.APPEND -> {
                    val remoteKey = database.withTransaction {
                        chatRemoteKeysDao.remoteKeyByChatId(chatID)
                    }

                    // You must explicitly check if the page key is null when
                    // appending, since null is only valid for initial load.
                    // If you receive null for APPEND, that means you have
                    // reached the end of pagination and there are no more
                    // items to load.
                    if (remoteKey.nextKey == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }

                    remoteKey.nextKey
                }
            }

            // Suspending network load via Retrofit. This doesn't need to
            // be wrapped in a withContext(Dispatcher.IO) { ... } block
            // since Retrofit's Coroutine CallAdapter dispatches on a
            // worker thread.
            val response: ChatRepoResult = suspendCoroutine { continuation ->
                var resumed = false
                chatRepo.getMessages(
                    conversationId = chatID.toInt(),
                    start = loadKey,
                    end = loadKey + state.config.pageSize,
                callback = {
                    if (!resumed){
                        continuation.resume(it)
                        resumed = true
                    }
                })
            }

            val result = if (response is ChatRepoResult.Success){
                response.data
            }else{
                return MediatorResult.Error((response as ChatRepoResult.Error).error)
            }

            val isEndOfList = result.isEmpty()

            val nextKey = if (isEndOfList) null else loadKey + 20

            // Store loaded data, and next key in transaction, so that
            // they're always consistent.
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    chatRemoteKeysDao.deleteByChatId(chatID)
                    messageDao.deleteByChatId(chatID)
                }


                // Update RemoteKey for this query.
                chatRemoteKeysDao.insertOrReplace(
                    ChatRemoteKey(chatID, nextKey)
                )

                // Insert new users into database, which invalidates the
                // current PagingData, allowing Paging to present the updates
                // in the DB.
                messageDao.insertAll(transform(result))
            }

            MediatorResult.Success(
                endOfPaginationReached = nextKey == null
            )
        } catch (e: Error) {
            MediatorResult.Error(e)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private fun transform(plains: List<Message>): List<Message> {
        val result: MutableList<Message> = ArrayList()
        plains.forEach {
            val message = map(it)
            result.add(message)
        }
        return result
    }

    private fun map(messagePlain: Message): Message {
        return Message(
            messageID = messagePlain.messageID,
            date = messagePlain.date * 1000L,
            text = messagePlain.text,
            userID = messagePlain.userID,
            chatID = messagePlain.chatID,
            myMsg = messagePlain.myMsg,
            fromMessenger = messagePlain.fromMessenger,
            isRead = messagePlain.isRead,
            photos = messagePlain.photos
        )
    }


}
