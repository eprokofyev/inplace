package com.inplace.chats

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.inplace.api.CommandResult
import com.inplace.api.vk.ApiVK
import com.inplace.api.vk.VkChatWithUsers
import com.inplace.api.vk.VkSingleton
import com.inplace.models.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ChatsRepo {

    private val executor: Executor = Executors.newSingleThreadExecutor()

    private var vkChats: MutableLiveData<MutableList<Chat>> = MutableLiveData<MutableList<Chat>>()

    fun getChats() = vkChats

    fun refresh() {
        executor.execute {
            val l = getChatsFromVK(0, 5)
            Log.d("len", l.size.toString())
            vkChats.postValue(l)

        }
    }

    private fun getChatsFromVK(start: Int, end: Int): MutableList<Chat> {
        val chats = mutableListOf<Chat>()
        val result: CommandResult = ApiVK.getChatsWithUsers(start, end)
        if (result.result is VkChatWithUsers) {
            val chatWithUsers = result.result as VkChatWithUsers
            Log.d("s", chatWithUsers.chats.size.toString())
            for (chat in chatWithUsers.chats) {
                Log.d("ch", chat.text)
                Log.d("users",chatWithUsers.users.get(chat.chatWithId).toString())
                val sobesednik = chatWithUsers.users[chat.lastMsgFromId]?.let {
                    SobesednikVk(it.firstName + it.lastName,
                            it.photo200Square,
                            it.id.toString(),
                            "today",
                            it.about,
                            0)
                }?.let {
                    Sobesednik(it.name, it.avatar, it, null, "0")
                }

                val message = Message(chat.date.toLong(),
                        chat.text, chat.lastMsgFromId,
                        VkSingleton.getUserId() == chat.lastMsgFromId,
                        Source.VK
                )
                if (sobesednik != null) {
                    Log.d("sob", "sob")
                }
                if (sobesednik != null && ChatsFragment.user != null) {
                    chats.add(Chat(ChatsFragment.user!!,
                            mutableListOf<Sobesednik>(sobesednik),
                            mutableListOf(message),
                            true,
                            "",
                            "",
                            Source.VK,
                            ""
                    ))

                }

            }
        } else {
            Log.d("err", result.errTextMsg)
        }

        return chats

    }

}

data class RepoChat(var sobesedniks: MutableList<Sobesednik>,
                    var messages: MutableList<Message>,
                    var isHeard: Boolean,
                    val conversationVkId: String,
                    var conversationTelegramId: String,
                    val localId: String
)