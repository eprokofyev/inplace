package com.inplace.chats.repository.vk

import android.util.Log
import androidx.paging.PagingSource
import androidx.room.*
import com.inplace.models.VKChat

@Dao
interface ChatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<VKChat>)

    @Query("SELECT * FROM VKChat ORDER BY last_date DESC")
    fun pagingSource(): PagingSource<Int, VKChat>


    //@Query("SELECT * FROM VKChat WHERE chatID IN :ids")
    //suspend fun getChats(ids: List<Long>): List<VKChat>

    @Query("SELECT * FROM VKChat WHERE chatID = :id")
    suspend fun getChat(id: Long): List<VKChat>

    @Query("DELETE FROM VKChat")
    suspend fun deleteChats()

    @Query("UPDATE VKChat SET outRead = :outRead WHERE chatID = :chatID")
    suspend fun updateOutRead(outRead:Int,chatID:Long)

    @Query("UPDATE VKChat SET inRead = :inRead WHERE chatID = :chatID")
    suspend fun updateInRead(inRead:Int,chatID:Long)
}