package com.inplace.api.vk

import android.util.Log
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

class SendMessageCommand(private val conversationId: Int, private val msg: String, private val photoUrls: ArrayList<ImageStruct>): ApiCommand<Int>() {
    override fun onExecute(manager: VKApiManager): Int {

        // make photos attachment
        var attachmentValue = ""
        for (i in 0 until photoUrls.size ) {
            attachmentValue += "photo" + photoUrls.get(i).ownerId.toString() + "_" + photoUrls.get(i).mediaId.toString()
            if (i + 1 != photoUrls.size) {
                attachmentValue += ","
            }
        }
//        Log.d("send msg", "attachmentValue:" + attachmentValue)

        val call = VKMethodCall.Builder()
            .method("messages.send")
            .args("random_id", VkSingleton.getNextNumber().toString())
            .args("user_id", conversationId.toString())
            .args("peer_id",  conversationId.toString())
            .args("message", msg)
            .args("attachment", attachmentValue)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParser())
    }


    private class ResponseApiParser : VKApiResponseParser<Int> {

        override fun parse(response: String): Int {
            try {
                val rootJson = JSONObject(response).getString("response")
                Log.d("send msg", "json response:" + response)
                return rootJson.toInt()
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }

        }
    }
}