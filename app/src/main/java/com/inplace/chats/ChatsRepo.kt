package com.inplace.chats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.inplace.api.CommandResult
import com.inplace.api.vk.ApiVK
import com.inplace.api.vk.VkChat
import com.inplace.api.vk.VkUser
import com.inplace.models.*
import java.util.ArrayList
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ChatsRepo {

    private val executor: Executor = Executors.newSingleThreadExecutor()

    private lateinit var vkChats: MutableLiveData<MutableList<VkChat>>

    fun getChats(): LiveData<MutableList<Chat>> {

    }

    fun refresh() {
        executor.execute {
            val result: CommandResult = ApiVK.getChats(0, 10)
            if (result.result != null) {
                if (result.result is ArrayList<*>) {
                    val array: ArrayList<*> = result.result as ArrayList<*>
                    val ids = arrayListOf<Int>()
                    for (el in array) {
                        if (el is VkChat) {
                            ids.add(el.lastMsgFromId)
                        }
                    }

                    getUser(ids)


                } else {
                    Log.d("error", "can't cast")
                }
            }

            if (result.error != null) {
                Log.d("error", result.errTextMsg )
            }
        }

    }

    private fun getChatsFromVK(start: Int, end: Int): ArrayList<Chat> {
        val chats = arrayListOf<Chat>()
        val result: CommandResult = ApiVK.getChats(0, 10)
        if (result.result is ArrayList<*>) {
            val m = mutableMapOf<Int, VkChat>()
            val ids = arrayListOf<Int>()
            for (chat in result.result as ArrayList<*>) {
                if (chat is VkChat) {
                    m[chat.lastMsgFromId] = VkChat()
                    ids.add(chat.lastMsgFromId)
                }
            }
            val users = getUserFromVK(ids)
            for (user in users) {
                val c = m[user.id.toInt()]
                if (c != null) {
                    Message(12, c.text, c.lastMsgFromId, )
                }
                Chat(User(), MutableList(1) { user },)
            }

        } else {
            throw Throwable("bad users")
        }

        return users
    }