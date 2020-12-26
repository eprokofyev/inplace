package com.inplace.api.vk

import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap


class GetChatsByIdCommand(private val ids: ArrayList<Int>): ApiCommand<VkChatWithUsers>() {
    override fun onExecute(manager: VKApiManager): VkChatWithUsers {

        var idsStr = ""
        for (i in 0 until ids.size ) {
            idsStr += ids.get(i).toString()
            if (i != ids.size - 1) {
                idsStr += ","
            }
        }

        val call = VKMethodCall.Builder()
            .method("messages.getChat")
            .args("chat_ids", idsStr)
            .args("fields", "about,status,online,photo_200")
            .version(manager.config.version)
            .build()

        return manager.execute(call, ResponseApiParser())

    }


    private class ResponseApiParser : VKApiResponseParser<VkChatWithUsers> {
        override fun parse(response: String): VkChatWithUsers {

            val vkChatWithUsers = VkChatWithUsers()
            val chatList = ArrayList<VkChat>()
            val chatUsers = HashMap<Int, VkUser>()

            try {

                val jsonChatsArray = JSONObject(response).getJSONArray("response")

                for (i in 0 until jsonChatsArray.length()) {


                    val vkChat = VkChat()

                    vkChat.chatType = VkChat.CHAT_TYPE_GROUP_CHAT

                    vkChat.chatWithId =
                        jsonChatsArray.getJSONObject(i).getString("id").toInt()

                    vkChat.groupChatTitle =
                        jsonChatsArray.getJSONObject(i).getString("title")

                    chatList.add(vkChat)

                    val jsonUsers: JSONArray = jsonChatsArray
                        .getJSONObject(i)
                        .getJSONArray("users")

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

                        //vkUser.isClosed = true


                        vkUser.status = oneUserJsonObj.getString("status")
                        val online = oneUserJsonObj.getString("online")
                        vkUser.online = online == "1"
                        vkUser.photo200Square = oneUserJsonObj.getString("photo_200")
                        chatUsers[vkUser.id] = vkUser
                    }

                    if (bannedUsers.size != 0) {
                        clearBannedUsers(chatList, chatUsers, bannedUsers)
                    }
                }

                vkChatWithUsers.chats = chatList
                vkChatWithUsers.users = chatUsers

                return vkChatWithUsers
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }



        private fun clearBannedUsers(chats: ArrayList<VkChat>, users: HashMap<Int, VkUser>, banned : ArrayList<Int>){
            for (idBanned in banned) {
                users.remove(idBanned)
            }
            for (chat in chats) {
                for (idBanned in banned) {
                    if (chat.chatWithId == idBanned) {
                        chats.remove(chat)
                    }
                }
            }
        }

    }
}