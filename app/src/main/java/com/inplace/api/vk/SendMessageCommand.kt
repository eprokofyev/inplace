package com.inplace.api.vk

import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

class SendMessageCommand(private val conversationId: Int, private val msg: String): ApiCommand<Int>() {
    override fun onExecute(manager: VKApiManager): Int {

        val call = VKMethodCall.Builder()
            .method("messages.send")
            .args("random_id", VkSingleton.getNextNumber().toString())
            .args("user_id", conversationId.toString())
            .args("peer_id",  conversationId.toString())
            .args("message", msg)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParser())

    }


    private class ResponseApiParser : VKApiResponseParser<Int> {

        override fun parse(response: String): Int {
            try {
                val rootJson = JSONObject(response).getString("response")
                return rootJson.toInt()
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }

        }
    }
}