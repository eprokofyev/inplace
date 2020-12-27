package com.inplace.api.vk

import android.util.Log
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class GetConversationsById(private val ids: java.util.ArrayList<Int>): ApiCommand<VkChatWithUsers>() {
    override fun onExecute(manager: VKApiManager): VkChatWithUsers {

        var idsStr = ""
        for (i in 0 until ids.size ) {
            idsStr += ids.get(i).toString()
            if (i != ids.size - 1) {
                idsStr += ","
            }
        }

        val call = VKMethodCall.Builder()
            .method("messages.getConversationsById")
            .args("peer_ids", idsStr)
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
                        jsonConversations.getJSONObject(i)
                            .getJSONObject("peer")
                            .getString("type")

                    vkChat.chatWithId =
                        jsonConversations.getJSONObject(i)
                            .getJSONObject("peer").getString("id").toInt()

                    vkChat.inRead = jsonConversations.getJSONObject(i)
                        .getString("in_read").toInt()

                    vkChat.outRead = jsonConversations.getJSONObject(i)
                        .getString("out_read").toInt()


                    try {
                        vkChat.unreadСount = jsonConversations.getJSONObject(i)
                            .getString("unread_count").toInt()
                    } catch (e: Exception) {
                        vkChat.unreadСount = 0
                    }

                    vkChat.lasMsgId =
                        jsonConversations.getJSONObject(i).getString("last_message_id").toInt()

                    if (type == "user") {
                        vkChat.chatType = VkChat.CHAT_TYPE_USER
                        chatList.add(vkChat)
                        continue
                    }

                    if (type == "chat") {
                        vkChat.chatType = VkChat.CHAT_TYPE_GROUP_CHAT
                        vkChat.groupChatTitle =
                            jsonConversations.getJSONObject(i)
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
                    val isClosed = oneUserJsonObj.getString("is_closed")
                    vkUser.isClosed = isClosed == "true"

                    vkUser.status = oneUserJsonObj.getString("status")
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