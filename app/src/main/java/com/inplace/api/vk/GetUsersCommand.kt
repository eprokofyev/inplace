package com.inplace.api.vk

import android.util.Log
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class GetUsersCommand(private val userIds: IntArray = intArrayOf()): ApiCommand<ArrayList<VkUser>>() {
    override fun onExecute(manager: VKApiManager): ArrayList<VkUser> {

        if (userIds.isEmpty()) {
            val call = VKMethodCall.Builder()
                .method("users.get")
                .args("fields", "about,status,online,photo_200")
                .version(manager.config.version)
                .build()
            return manager.execute(call, ResponseApiParser())
        } else {
            val result = ArrayList<VkUser>()
            val chunks = userIds.toList().chunked(CHUNK_LIMIT)
            for (chunk in chunks) {
                val call = VKMethodCall.Builder()
                    .method("users.get")
                    .args("user_ids", chunk.joinToString(","))
                    .args("fields", "about,status,online,photo_200")
                    .version(manager.config.version)
                    .build()
                result.addAll(manager.execute(call, ResponseApiParser()))
            }
            return result
        }
    }

    companion object {
        const val CHUNK_LIMIT = 900
    }

    private class ResponseApiParser : VKApiResponseParser<ArrayList<VkUser>> {
        override fun parse(response: String): ArrayList<VkUser> {
            try {
                val ja = JSONObject(response).getJSONArray("response")

                val body = JSONObject(response).getString("response")
                Log.d("body:", body)

                val r = ArrayList<VkUser>(ja.length())
                for (i in 0 until ja.length()) {
                    val user = VkUser.parse(ja.getJSONObject(i))
                    r.add(user)
                }
                return r
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}