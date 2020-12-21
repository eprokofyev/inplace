package com.inplace.chats.repository.vk

import androidx.room.*

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remotekeys WHERE chatID = :id")
    suspend fun remoteKeysDoggoId(id: Long): RemoteKeys?

    @Query("DELETE FROM remotekeys")
    suspend fun clearRemoteKeys()
}

@Entity
data class RemoteKeys(@PrimaryKey val chatID: Long, val prevKey: Int?, val nextKey: Int?)