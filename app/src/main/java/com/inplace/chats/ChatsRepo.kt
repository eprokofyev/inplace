package com.inplace.chats

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.inplace.api.ApiImageLoader
import com.inplace.api.CommandResult
import com.inplace.api.vk.*
import com.inplace.models.*
import java.lang.Exception
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ChatsRepo {

    private val executor: Executor = Executors.newSingleThreadExecutor()

    private var vkChats: MutableLiveData<MutableList<SuperChat>> = MutableLiveData<MutableList<SuperChat>>()

    fun getChats() = vkChats


    fun refresh(context: Context, start: Int, end: Int, callback: OnRequestCompleteListener) {
        executor.execute {
            val result = getChatsFromVK(context, start, end)
            if (result is RepoResult.Success) {
                vkChats.postValue(result.data)
                Log.d("tag", result.data.size.toString())
            }

            callback.onRequestComplete(result)
        }
    }

    private fun getChatsFromVK(context: Context, start: Int, end: Int): RepoResult {
        val chats = mutableListOf<SuperChat>()
        val result = ApiVK.getChats(start, end)

        if (result.error != null) {
            return RepoResult.Error(Exception(result.errTextMsg))
        }

        val vkChat = result.result?.chats ?: arrayListOf<VkChat>()
        val vkUsers = result.result?.users ?: hashMapOf<Int, VkUser>()


        for (chat in vkChat) {

            var sobesednik: SuperSobesednik? = null

            var image = ""
            var profileName = chat.groupChatTitle
            if (chat.chatType == VkChat.CHAT_TYPE_USER) {
                Log.d("gg", "hh")
                Log.d("gg", Thread.currentThread().name)
                sobesednik = vkUsers[chat.chatWithId]?.let {
                    VKSobesednik(
                        it.firstName + " "
                                + it.lastName,
                        it.photo200Square,
                        it.id.toString(),
                        "today",
                        it.about,
                        0
                    )
                }?.let {
                    Sobesednik(it.name, it.avatar, it, null, "0")
                }
                profileName = vkUsers[chat.chatWithId]?.firstName ?: "" + " " + vkUsers[chat.chatWithId]?.lastName ?: ""
                image = sobesednik?.avatar ?: ""
            }

            Log.d("tag", profileName)

            if (profileName == null) {
                profileName = chat.groupChatTitle
            }

            val message = Message(
                0, chat.date.toLong(),
                chat.text, chat.lastMsgFromId,
                ChatsFragment.user?.id ?: 0 == chat.lastMsgFromId,
                Source.VK,
                false,
                arrayListOf(),
            )

            if (sobesednik != null) {
                Log.d("sob", "sob")
            }

            chats.add(
                if (sobesednik != null && ChatsFragment.user != null) {
                    Chat(
                        ChatsFragment.user!!,
                        profileName,
                        when (image) {
                            "" -> null
                            else -> ApiImageLoader.getImageByUrl(image, context)
                        },
                        mutableListOf<Sobesednik>(sobesednik),
                        mutableListOf(message),
                        true,
                        when (chat.chatType) {
                            VkChat.CHAT_TYPE_GROUP_CHAT -> false
                            else -> true
                        },
                        chat.chatWithId.toString(),
                        "",
                        Source.VK,
                        ""
                    )
                } else {
                    Chat(
                        ChatsFragment.user!!,
                        profileName,
                        when (image) {
                            "" -> null
                            else -> ApiImageLoader.getImageByUrl(image, context)
                        },
                        mutableListOf<Sobesednik>(),
                        mutableListOf(message),
                        true,
                        when (chat.chatType) {
                            VkChat.CHAT_TYPE_GROUP_CHAT -> false
                            else -> true
                        },
                        chat.chatWithId.toString(),
                        "",
                        Source.VK,
                        ""
                    )
                }
            )

        }

        return RepoResult.Success(chats)
    }

    interface OnRequestCompleteListener {
        fun onRequestComplete(result: RepoResult)
    }

}

data class RepoChat(
    var sobesedniks: MutableList<Sobesednik>,
    var messages: MutableList<Message>,
    var isHeard: Boolean,
    val conversationVkId: String,
    var conversationTelegramId: String,
    val localId: String
)