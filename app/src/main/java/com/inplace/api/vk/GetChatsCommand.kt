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
import kotlin.collections.ArrayList

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
                            .getJSONObject("peer").getString("id").toInt()

                    vkChat.inRead = jsonConversations.getJSONObject(i)
                        .getJSONObject("conversation").getString("in_read").toInt()

                    vkChat.outRead = jsonConversations.getJSONObject(i)
                        .getJSONObject("conversation").getString("out_read").toInt()


                    try {
                        vkChat.unreadСount = jsonConversations.getJSONObject(i)
                            .getJSONObject("conversation")
                            .getString("unread_count").toInt()
                    } catch (e: Exception) {
                        vkChat.unreadСount = 0
                    }


                    val lastMessageObj: JSONObject =
                        jsonConversations.getJSONObject(i).getJSONObject("last_message")

                    vkChat.text = lastMessageObj.getString("text")
                    vkChat.date = lastMessageObj.getString("date").toLong()
                    vkChat.lastMsgFromId = lastMessageObj.getString("from_id").toInt()
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



                // get users
                val chatUsers = HashMap<Int, VkUser>()

                val jsonUsers: JSONArray = rootJson.getJSONArray("profiles")

                val bannedUsers = ArrayList<Int>()

                for (i in 0 until jsonUsers.length()) {
                    val vkUser = VkUser()
                    val oneUserJsonObj = jsonUsers.getJSONObject(i)

                    if (oneUserJsonObj.getString("first_name").equals("DELETED")) {
                        continue
                    }

                    vkUser.id = oneUserJsonObj.getString("id").toInt()

                    try {
                        val banValue = oneUserJsonObj.getString("deactivated")
                        if (banValue == "banned") {
                            bannedUsers.add(vkUser.id)
                            continue
                        }
                    } catch (e: JSONException) {
                        // nothing, user have no ban
                    }

                    // skip group chat
                    if (vkUser.id > ApiVk.START_ID_GROUP_CHAT) continue

                    vkUser.firstName = oneUserJsonObj.getString("first_name")
                    vkUser.lastName = oneUserJsonObj.getString("last_name")
                    var isClosed = false
                    try {
                        isClosed = oneUserJsonObj.getString("is_closed") == "true"
                    } catch (ex: Exception) {
                        isClosed = false
                    }

                    vkUser.isClosed = isClosed

                    try {
                        vkUser.status = oneUserJsonObj.getString("status")
                    } catch (e: JSONException) {
                        vkUser.status = ""
                    }
                    val online = oneUserJsonObj.getString("online")
                    vkUser.online = online == "1"
                    vkUser.photo200Square = oneUserJsonObj.getString("photo_200")
                    chatUsers[vkUser.id] = vkUser
                }

                if (bannedUsers.size != 0) {
                    VkChatWithUsers().clearBannedUsers(chatList, chatUsers, bannedUsers)
                }

                vkChatWithUsers.chats = chatList
                vkChatWithUsers.users = chatUsers

                return vkChatWithUsers
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }

    }
}