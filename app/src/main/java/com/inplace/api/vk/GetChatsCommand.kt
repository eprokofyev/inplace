package com.inplace.api.vk

import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class GetChatsCommand(private val start: Int, private val end: Int): ApiCommand<VkChatWithUsers>() {
    override fun onExecute(manager: VKApiManager): VkChatWithUsers {

        val count = end - start

        val call = VKMethodCall.Builder()
         .method("messages.getConversations")
         .args("offset", start.toString())
         .args("count", count.toString())
         .args("extended", "1")
         .args("fields", "about,status,online,photo_200")
         .version(manager.config.version)
         .build()
         return manager.execute(call, ResponseApiParser())

    }


    private class ResponseApiParser : VKApiResponseParser<VkChatWithUsers> {
        override fun parse(response: String): VkChatWithUsers {
            try {
                val rootJson = JSONObject(response).getJSONObject("response")

                val vkChatWithUsers = VkChatWithUsers()

                val jsonConversations = rootJson.getJSONArray("items")

                // get chats
                val chatList = ArrayList<VkChat>()

                for (i in 0 until jsonConversations.length()) {
                    val vkChat = VkChat()
                    val type: String =
                        jsonConversations.getJSONObject(i).getJSONObject("conversation")
                            .getJSONObject("peer").getString("type")

                    vkChat.chatWithId =
                        jsonConversations.getJSONObject(i).getJSONObject("conversation")
                            .getJSONObject("peer").getString("id").toLong()

                    val lastMessageObj: JSONObject =
                        jsonConversations.getJSONObject(i).getJSONObject("last_message")

                    vkChat.text = lastMessageObj.getString("text")
                    vkChat.date = lastMessageObj.getString("date").toInt()
                    vkChat.lastMsgFromId = lastMessageObj.getString("from_id").toLong()
                    vkChat.lasMsgId = lastMessageObj.getString("id").toInt()

                    if (type == "user") {
                        vkChat.chatType = VkChat.CHAT_TYPE_USER
                        chatList.add(vkChat)
                        continue
                    }

                    if (type == "chat") {
                        vkChat.chatType = VkChat.CHAT_TYPE_GROUP_CHAT
                        vkChat.groupChatTitle =
                            jsonConversations.getJSONObject(i).getJSONObject("conversation")
                                .getJSONObject("chat_settings").getString("title")
                        chatList.add(vkChat)
                        continue
                    }
                }

                vkChatWithUsers.chats = chatList

                val START_ID_GROUP_CHAT = 2000000000

                // get users
                val chatUsers = HashMap<Long, VkUser>()

                val jsonUsers: JSONArray = rootJson.getJSONArray("profiles")

                for (i in 0 until jsonUsers.length()) {
                    val vkUser = VkUser()
                    val oneUserJsonObj = jsonUsers.getJSONObject(i)
                    vkUser.id = oneUserJsonObj.getString("id").toLong()

                    // skip group chat
                    if (vkUser.id > START_ID_GROUP_CHAT) continue

                    vkUser.firstName = oneUserJsonObj.getString("first_name")
                    vkUser.lastName = oneUserJsonObj.getString("last_name")
                    val isClosed = oneUserJsonObj.getString("is_closed")
                    vkUser.isClosed = isClosed == "true"

                    vkUser.status = oneUserJsonObj.getString("status")
                    val online = oneUserJsonObj.getString("online")
                    vkUser.online = online == "1"
                    vkUser.photo200Square = oneUserJsonObj.getString("photo_200")
                    chatUsers[vkUser.id] = vkUser
                }

                vkChatWithUsers.users = chatUsers

                return vkChatWithUsers
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}