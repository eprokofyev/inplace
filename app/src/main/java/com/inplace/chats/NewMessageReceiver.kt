package com.inplace.chats

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.room.withTransaction
import com.inplace.api.vk.ApiVk
import com.inplace.api.vk.VkUser
import com.inplace.db.AppDatabase
import com.inplace.models.*
import com.inplace.services.ExecutorServices
import com.inplace.services.NotificationService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume

@ExperimentalPagingApi
class NewMessageReceiver(context: Context) : BroadcastReceiver() {

    val executor = ExecutorServices.getInstanceDB()

    val repo = ChatsRepo(context)

    val db = AppDatabase.getInstance(context)

    override fun onReceive(context: Context?, intent: Intent?) {
        val newMessages = intent?.getParcelableArrayListExtra<Message>(NotificationService.EXTRAS_NAME)

        Log.d("user", newMessages?.size.toString())

        val chatsDao = db.getChatsDao()

        val map = newMessages?.groupBy {
            it.chatID
        }?.mapValues {
            it.value.sortedWith(object : Comparator<Message> {
                override fun compare(o1: Message?, o2: Message?): Int {
                    if(o1 == null || o2 == null)
                        return 0

                    return o2.date.compareTo(o1.date)
                }
            })
        }

        val idsForNetwork = arrayListOf<Int>()
        map?.let { map ->
            executor.execute {
                val chats = chatsDao.getChats(map.keys.toList())
                Log.d("receiver j", chats.size.toString())
                chats.forEach { chat ->
                    map.get(chat.chatID)?.let { list ->
                        list.firstOrNull()?.let {
                            chat.lastMessage = it
                            var inReadFlag = true
                            var outReadFlag = true
                            list.forEach {
                                if (it.isRead && it.myMsg && inReadFlag) {
                                    chat.inRead = it.chatID.toInt()
                                    inReadFlag = false
                                }

                                if (it.isRead && !it.myMsg && outReadFlag) {
                                    chat.outRead = it.chatID.toInt()
                                    outReadFlag = false
                                }

                                if (!it.isRead) {
                                    chat.unReadCount++
                                }
                            }
                        }
                    }
                    if (!map.keys.toList().contains(chat.chatID)) {
                        idsForNetwork.add(chat.chatID.toInt())
                    }
                }
                GlobalScope.launch {
                    chatsDao.insertChats(chats)
                }

                repo.getVKChatsByIds(idsForNetwork, object : ChatsRepo.OnRequestCompleteListener {
                    override fun onRequestComplete(result: ResultVKChat) {
                        if (result is ResultVKChat.Success) {
                            result.data.forEach{chat ->
                                map.get(chat.chatID)?.firstOrNull()?. let {
                                    chat.lastMessage = it
                                }
                            }
                            GlobalScope.launch {
                                chatsDao.insertChats(result.data)
                            }
                        }
                    }
                })
            }
        }



        Log.d("receiver",newMessages?.firstOrNull().toString())

        Log.d("receiver",newMessages?.size.toString())

        Log.d("receiver", "heloo")
    }
}