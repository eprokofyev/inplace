package com.inplace.api.vk

import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject


class MarkAsReadCommand(private val peerId: Int): ApiCommand<Boolean>() {
    override fun onExecute(manager: VKApiManager): Boolean {

        val call = VKMethodCall.Builder()
            .method("messages.markAsRead")
            .args("peer_id", peerId)
            .args("mark_conversation_as_read", "1")
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParser())
    }

    private class ResponseApiParser : VKApiResponseParser<Boolean> {
        override fun parse(response: String): Boolean {
            try {
               return JSONObject(response).getString("response") == "1"
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }

    }
}