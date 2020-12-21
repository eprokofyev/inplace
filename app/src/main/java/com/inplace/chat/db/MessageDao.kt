package com.inplace.chat.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.inplace.models.Message

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<Message>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: Message)

    @Query("SELECT * FROM messages WHERE chat_id = :chatID")
    fun pagingSource(chatID: Long): PagingSource<Int, Message>

    @Query("DELETE FROM messages WHERE chat_id = :chatID")
    suspend fun deleteByChatId(chatID: Long)

    @Query("DELETE FROM messages")
    suspend fun clearAll()
}