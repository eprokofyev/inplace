package com.inplace.api.vk

import com.inplace.models.Message
import com.inplace.models.Source
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList


class GetMessagesHistoryCommand(private val start: Int, private val end: Int, private val conversationId: Int): ApiCommand<ArrayList<Message>>() {
    override fun onExecute(manager: VKApiManager): ArrayList<Message> {

        val count = end - start

        val call = VKMethodCall.Builder()
            .method("messages.getHistory")
            .args("fields", "about,status,online,photo_200")
            .args("user_id", conversationId.toString())
            .args("peer_id", conversationId.toString())
            .args("count", count)
            .args("offset", start)
            .args("extended", "1")

            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParser())
    }


    private class ResponseApiParser : VKApiResponseParser<ArrayList<Message>> {
        override fun parse(response: String): ArrayList<Message> {
            try {
                val rootJson = JSONObject(response).getJSONObject("response")

                val jsonMessages: JSONArray = rootJson.getJSONArray("items")

                val messages = ArrayList<Message>()

                for (i in 0 until jsonMessages.length()) {
                    val message = Message(0,0,"",0,0,false, Source.VK,false, arrayListOf())
                    val oneMessageJsonObj = jsonMessages.getJSONObject(i)
                    message.text = oneMessageJsonObj.getString("text")

                    // add only text msg and photos
                    var isText = true
                    if (message.text == "") {
                        isText = false
                    }
                    message.userID = oneMessageJsonObj.getString("from_id").toLong()
                    message.date = oneMessageJsonObj.getString("date").toLong()
                    if (message.userID == VK.getUserId().toLong()) {
                        message.myMsg = true
                    }
                    message.messageID = oneMessageJsonObj.getString("id").toInt()
                    message.fromMessenger = Source.VK

                    var attachmentsArray :JSONArray
                    try {
                        attachmentsArray = oneMessageJsonObj.getJSONArray("attachments")
                    } catch (ex: Exception) {
                        if (isText) {
                            messages.add(message)
                        }
                        continue
                    }

                    for (j in 0 until attachmentsArray.length()) {

                        val oneAttachment = attachmentsArray.getJSONObject(j)

                        val type = oneAttachment.getString("type")

                        if (!type.equals("photo")) {
                            continue
                        }

                        val url = oneAttachment.getJSONObject("photo").getJSONArray("sizes")
                            .getJSONObject(2)
                            .getString("url")

                        message.photos.add(url)
                    }


                    messages.add(message)
                }
                return messages
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}