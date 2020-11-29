package com.inplace.chats

import android.app.Application
import android.graphics.Bitmap
import java.util.GregorianCalendar
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.inplace.api.ApiImageLoader
import com.inplace.api.CommandResult
import com.inplace.api.vk.ApiVK
import com.inplace.api.vk.VkChat
import com.inplace.api.vk.VkChatWithUsers
import com.inplace.api.vk.VkSingleton
import com.inplace.models.*
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ChatsRepo(val application: Application) {

    private val executor: Executor = Executors.newSingleThreadExecutor()

    private var vkChats: MutableLiveData<MutableList<Chat>> = MutableLiveData<MutableList<Chat>>()

    fun getChats() = vkChats

    fun refresh() {
        executor.execute {
            val l = getChatsFromVK(0, 5)
            vkChats.postValue(l)

        }
    }

    private fun getChatsFromVK(start: Int, end: Int): MutableList<Chat> {
        val chats = mutableListOf<Chat>()
        val result: CommandResult = ApiVK.getChatsWithUsers(start, end)
        if (result.result is VkChatWithUsers) {
            val chatWithUsers = result.result as VkChatWithUsers
            for (chat in chatWithUsers.chats) {

                var sobesednik:Sobesednik? = null

                var image = ""
                var profileName = chat.groupChatTitle
                if (chat.chatType == VkChat.CHAT_TYPE_USER) {
                    Log.d("gg", "hh")
                    sobesednik = chatWithUsers.users[chat.chatWithId]?.let {
                        SobesednikVk(it.firstName + " "
                                + it.lastName,
                            it.photo200Square,
                            it.id.toString(),
                            "today",
                            it.about,
                            0)
                    }?.let {
                        Sobesednik(it.name, it.avatar, it, null, "0")
                    }
                    profileName = chatWithUsers.users[chat.chatWithId]?.firstName ?: "" + " " + chatWithUsers.users[chat.chatWithId]?.lastName ?: ""
                    image = sobesednik?.avatar ?: ""
                }

                val message = Message(chat.date.toLong(),
                        chat.text, chat.lastMsgFromId,
                        VkSingleton.getUserId() == chat.lastMsgFromId,
                        Source.VK
                )

                if (sobesednik != null) {
                    Log.d("sob", "sob")
                }

                chats.add(if (sobesednik != null && ChatsFragment.user != null) {
                    Chat(ChatsFragment.user!!,
                            profileName,
                            when (image) {
                                "" -> null
                                else -> ApiImageLoader.getImageByUrl(image, this.application)
                            },
                            mutableListOf<Sobesednik>(sobesednik),
                            mutableListOf(message),
                            true,
                            when(chat.chatType) {
                                VkChat.CHAT_TYPE_GROUP_CHAT -> false
                                else -> true
                            },
                            "",
                            "",
                            Source.VK,
                            ""
                    )
                } else {
                    Chat(ChatsFragment.user!!,
                        profileName,
                        when (image) {
                            "" -> null
                            else -> ApiImageLoader.getImageByUrl(image, this.application)
                        },
                        mutableListOf<Sobesednik>(),
                        mutableListOf(message),
                        true,
                        when(chat.chatType) {
                            VkChat.CHAT_TYPE_GROUP_CHAT -> false
                            else -> true
                        },
                        "",
                        "",
                        Source.VK,
                        ""
                    )
                })

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