package com.inplace.api.vk

import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

class GetUploadServerCommand(): ApiCommand<Boolean>() {
    override fun onExecute(manager: VKApiManager): Boolean {

        val call = VKMethodCall.Builder()
            .method("photos.getMessagesUploadServer")
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParser())
    }


    private class ResponseApiParser : VKApiResponseParser<Boolean> {

        override fun parse(response: String): Boolean {
            try {
                val uploadUrl = JSONObject(response)
                    .getJSONObject("response")
                    .getString("upload_url")
                VkSingleton.UploadServer.setInit(uploadUrl)
                return true;

            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }

        }
    }
}