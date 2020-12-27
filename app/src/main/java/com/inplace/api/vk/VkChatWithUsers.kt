package com.inplace.api.vk

import java.util.*

class VkChatWithUsers {

    var chats: ArrayList<VkChat>? = null

    // map [userId] = vkUser
    var users: HashMap<Int, VkUser>? = null




    fun clearBannedUsers(chats: ArrayList<VkChat>,
                         users: HashMap<Int, VkUser>,
                         banned :ArrayList<Int>) {
        for (idBanned in banned) {
            users.remove(idBanned)
        }
        var needDelete: ArrayList<VkChat> = ArrayList<VkChat>()
        for (chat in chats) {
            for (idBanned in banned) {
                if (chat.chatWithId == idBanned) {
                    needDelete.add(chat)
                }
            }
        }
        chats.removeAll(needDelete)
    }
}

