package com.inplace.api.vk

import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

class GetLongPullCommand(): ApiCommand<Boolean>() {
    override fun onExecute(manager: VKApiManager): Boolean {

        val call = VKMethodCall.Builder()
            .method("messages.getLongPollServer")
            .args("need_pts", "1")
            .args("lp_version", "3")
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParser())

    }


    private class ResponseApiParser : VKApiResponseParser<Boolean> {

        override fun parse(response: String): Boolean {
            try {
                val rootJson = JSONObject(response).getJSONObject("response")
                val server = rootJson.getString("server")
                val key = rootJson.getString("key")
                val ts = rootJson.getString("ts").toInt()
                VkSingleton.LongPollServer.setInit(server, key, ts);
                return true;

            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }

        }
    }
}