package com.inplace

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.inplace.chat.db.ChatRemoteKey
import com.inplace.chat.db.ChatRemoteKeyDao
import com.inplace.chat.db.MessageDao
import com.inplace.models.Message

@Database(version = 1, entities = [Message::class,ChatRemoteKey::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getMessageDao(): MessageDao
    abstract fun getChatRemoteKeysDao(): ChatRemoteKeyDao

    companion object {
        val INPLACE_DB = "inplace.db"


        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, INPLACE_DB)
                .build()
    }

}