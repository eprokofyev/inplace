package com.inplace.chat.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: ChatRemoteKey)

    @Query("SELECT * FROM chat_remote_keys WHERE chat_id = :chatID")
    suspend fun remoteKeyByChatId(chatID: Long): ChatRemoteKey

    @Query("DELETE FROM chat_remote_keys WHERE chat_id = :chatID")
    suspend fun deleteByChatId(chatID: Long)
}