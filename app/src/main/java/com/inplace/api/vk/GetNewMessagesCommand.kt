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
import java.lang.Exception


class GetNewMessagesCommand(): ApiCommand<ArrayList<Message>>() {
    override fun onExecute(manager: VKApiManager): ArrayList<Message> {

        var ptsParameterName = "ts"
        var ptsParameterValue = VkSingleton.LongPollServer.ts.toString()
        if (VkSingleton.LongPollServer.pts != -1) {
            ptsParameterName = "pts"
            ptsParameterValue = VkSingleton.LongPollServer.pts.toString()
        }

        val callAfterSetMethod = VKMethodCall.Builder()
            .method("messages.getLongPollHistory")
        val call = callAfterSetMethod.args(ptsParameterName, ptsParameterValue)
            .args("ts", VkSingleton.LongPollServer.ts.toString())
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParser())

    }


    private class ResponseApiParser : VKApiResponseParser<ArrayList<Message>> {

        override fun parse(response: String): ArrayList<Message> {
            try {
                val rootJson = JSONObject(response).getJSONObject("response")

                val messagesArray: JSONArray = rootJson.getJSONObject("messages")
                        .getJSONArray("items")

                val newMessages = java.util.ArrayList<Message>()

                for (i in 0 until messagesArray.length()) {
                    val oneMessageJSON = messagesArray.getJSONObject(i)
                    val message = Message(0,0,"",0,0,false, Source.VK,false, arrayListOf())
                    message.text = oneMessageJSON.getString("text")

                    // add only text msg and photos
                    var isText = true
                    if (message.text == "") {
                        isText = false
                    }

                    // skip deleting event
                    if (oneMessageJSON.toString().contains("deleted")) {
                        continue
                    }

                    message.userID = oneMessageJSON.getString("from_id").toLong()
                    message.date = oneMessageJSON.getString("date").toLong()
                    message.messageID = oneMessageJSON.getString("id").toInt()
                    if (message.userID == VK.getUserId().toLong()) {
                        message.myMsg = true
                    }
                    message.fromMessenger = Source.VK

                    var attachmentsArray :JSONArray
                    try {
                        attachmentsArray = oneMessageJSON.getJSONArray("attachments")
                    } catch (ex: Exception) {
                        if (isText) {
                            newMessages.add(message)
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

                    newMessages.add(message)
                }

                VkSingleton.LongPollServer.pts = rootJson.getString("new_pts").toInt()

                return newMessages

            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }

        }
    }
}