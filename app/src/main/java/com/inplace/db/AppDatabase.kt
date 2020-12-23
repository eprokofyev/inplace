package com.inplace.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.inplace.chat.db.ChatRemoteKey
import com.inplace.chat.db.ChatRemoteKeyDao
import com.inplace.chat.db.MessageDao
import com.inplace.chats.repository.vk.ChatsDao
import com.inplace.chats.repository.vk.RemoteKeys
import com.inplace.chats.repository.vk.RemoteKeysDao
import com.inplace.models.Message
import com.inplace.models.VKChat
import com.inplace.models.VKSobesednik


@Database(
    version = 2,
    entities = [Message::class, VKChat::class, VKSobesednik::class, RemoteKeys::class, ChatRemoteKey::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getChatsDao(): ChatsDao
    abstract fun getRemoteKeysDao(): RemoteKeysDao
    abstract fun getMessageDao(): MessageDao
    abstract fun getChatRemoteKeysDao(): ChatRemoteKeyDao

    companion object {

        val INPLACE_DB = "inplace.db"

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