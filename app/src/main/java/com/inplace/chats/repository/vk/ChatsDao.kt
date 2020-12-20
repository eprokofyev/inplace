package com.inplace.chats.repository.vk

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.inplace.models.VKChat

@Dao
interface ChatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<VKChat>)

    @Query("SELECT * FROM VKChat ORDER BY last_date DESC")
    fun pagingSource(): PagingSource<Int, VKChat>


    @Query("SELECT * FROM VKChat LIMIT :limit OFFSET :offset")
    suspend fun getChats(offset: Int, limit: Int): List<VKChat>

    @Query("DELETE FROM VKChat")
    suspend fun deleteChats()
}