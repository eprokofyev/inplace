package com.inplace.chat.db

import androidx.room.*

@Dao
interface ChatRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: ChatRemoteKey)

    @Query("SELECT * FROM chat_remote_keys WHERE chat_id = :chatID")
    suspend fun remoteKeyByChatId(chatID: Long): ChatRemoteKey

    @Query("DELETE FROM chat_remote_keys WHERE chat_id = :chatID")
    suspend fun deleteByChatId(chatID: Long)

    @Query("DELETE FROM chat_remote_keys")
    suspend fun clearRemoteKeys()
}