package com.inplace.chats

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.inplace.api.ApiImageLoader
import com.inplace.api.vk.*
import com.inplace.api.vk.VkChat.CHAT_TYPE_USER
import com.inplace.models.*
import com.inplace.services.ExecutorServices
import com.vk.api.sdk.VK
import java.lang.Exception
import java.util.concurrent.Executor

class ChatsRepo(
    private val vkChats: MutableLiveData<MutableList<VKChat>>,
    private val telegramChats: MutableLiveData<MutableList<TelegramChat>>,
    private val avatars: MutableLiveData<MutableList<Bitmap>>,
    ) {

    private val executor: Executor = ExecutorServices.getInstanceAPI()

    fun refresh(context: Context, start: Int, end: Int, callback: OnRequestCompleteListener) {
        executor.execute {
            val result = getVKChats(start, end)
            if (result is ResultVKChat.Success) {
                vkChats.postValue(result.data)
                Log.d("tag", result.data.size.toString())
            }

            callback.onRequestComplete(result)
        }
    }

    fun getVKChats(start: Int, end: Int): ResultVKChat {
        return getChatsFromVK(start, end)
    }

    private fun getChatsFromVK(start: Int, end: Int): ResultVKChat {
        val chats = mutableListOf<VKChat>()
        val result = ApiVK.getChats(start, end)

        if (result.error != null) {
            return ResultVKChat.Error(Exception(result.errTextMsg))
        }

        val vkChats = result.result?.chats ?: arrayListOf<VkChat>()
        val vkUsers = result.result?.users ?: hashMapOf<Int, VkUser>()


        for (vkChat in vkChats) {

            val msg = Message(
                vkChat.lasMsgId,
                vkChat.date,
                vkChat.text,
                vkChat.lastMsgFromId,
                vkChat.chatWithId,
                vkChat.lastMsgFromId == VK.getUserId().toLong(),
                Source.VK,
                false,
                arrayListOf(),
            )

            var vkUser = vkUsers[vkChat.lastMsgFromId]
            if (vkUser == null) {
                return ResultVKChat.Error(Exception("собеседник не найден"))
            }

            var sobesednik = VKSobesednik(
                vkUser.id,
                vkUser.firstName,
                vkUser.lastName,
                null,
                vkUser.photo200Square,
                if (vkUser.online) "online" else "",
                "about",
                0,
            )

            val usersMap = hashMapOf<Long, IVKSobesednik>()
            var title = vkChat.groupChatTitle
            var avatarUrl = ""
            var chatType = ChatType.GROUP
            if (vkChat.chatType == CHAT_TYPE_USER) {
                if (sobesednik.userID != vkChat.chatWithId) {
                    vkUser = vkUsers[vkChat.chatWithId]
                    if (vkUser == null) {
                        return ResultVKChat.Error(Exception("собеседник не найден"))
                    }

                    sobesednik = VKSobesednik(
                        vkUser.id,
                        vkUser.firstName,
                        vkUser.lastName,
                        null,
                        vkUser.photo200Square,
                        if (vkUser.online) "online" else "",
                        "about",
                        0,
                    )
                }

                title = sobesednik.name + " " + sobesednik.lastName
                avatarUrl = sobesednik.avatarUrl
                chatType = ChatType.PRIVATE
            }

            val chat = VKChat(
                vkChat.chatWithId,
                Source.VK,
                title,
                null,
                avatarUrl,
                arrayListOf(msg),
                true,
                chatType,
                msg,
                hashMapOf<Long, IVKSobesednik>(sobesednik.userID to SuperSobesednik(sobesednik, null, Source.VK)),
            )

            chats.add(chat)
        }

        return ResultVKChat.Success(chats)
    }

    interface OnRequestCompleteListener {
        fun onRequestComplete(result: ResultVKChat)
    }

}

sealed class ResultVKChat {
    data class Success(val data: MutableList<VKChat>) : ResultVKChat()
    data class Error(val error: Exception) : ResultVKChat()
}