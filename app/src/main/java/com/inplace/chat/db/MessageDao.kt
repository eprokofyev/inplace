package com.inplace.chat.db

import androidx.paging.PagingSource
import androidx.room.*
import com.inplace.models.Message
import com.inplace.models.MessageStatus
import com.inplace.models.MessageStatusConverter

@TypeConverters(MessageStatusConverter::class)
@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<Message>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: Message)

    @Query("UPDATE messages SET status = :status WHERE message_id = :messageID AND chat_id = :chatID")
    suspend fun updateMessageStatus(chatID: Long, messageID: Int,status: MessageStatus)

    @Query("UPDATE messages SET message_id = :newID WHERE message_id = :oldID AND chat_id = :chatID")
    suspend fun updateMessageID(chatID: Long, oldID: Int, newID:Int)

    @Query("SELECT * FROM messages WHERE chat_id = :chatID ORDER BY date DESC")
    fun pagingSource(chatID: Long): PagingSource<Int, Message>

    @Query("SELECT * FROM messages WHERE chat_id = :chatID AND myMsg LIKE '0'")
    fun getLastInMessage(chatID: Long):Message

    @Query("DELETE FROM messages WHERE chat_id = :chatID")
    suspend fun deleteByChatId(chatID: Long)

    @Query("DELETE FROM messages")
    suspend fun clearAll()
}