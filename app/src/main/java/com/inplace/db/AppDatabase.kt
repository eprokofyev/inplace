package com.inplace.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.inplace.chats.repository.vk.ChatsDao
import com.inplace.chats.repository.vk.RemoteKeys
import com.inplace.chats.repository.vk.RemoteKeysDao
import com.inplace.models.Message
import com.inplace.models.SourceConverter
import com.inplace.models.VKChat
import com.inplace.models.VKSobesednik
import com.vk.api.sdk.VK


@Database(version = 1, entities = [Message::class, VKChat::class,VKSobesednik::class, RemoteKeys::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getChatsDao(): ChatsDao

    abstract fun getRemoteKeysDao(): RemoteKeysDao

    companion object {

        val INPLACE_DB = "inpalce.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, INPLACE_DB)
                .fallbackToDestructiveMigration()
                .build()
    }

}