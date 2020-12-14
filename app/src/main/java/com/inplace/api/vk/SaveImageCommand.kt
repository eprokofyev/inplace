package com.inplace.api.vk


import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject


class SaveImageCommand(private val fileUploadInfo: FileUploadInfo): ApiCommand<ImageStruct>() {
    override fun onExecute(manager: VKApiManager): ImageStruct {

        val call = VKMethodCall.Builder()
            .method("photos.saveMessagesPhoto")
            .args("server", fileUploadInfo.server)
            .args("photo", fileUploadInfo.photo)
            .args("hash", fileUploadInfo.hash)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParser())
    }


    private class ResponseApiParser : VKApiResponseParser<ImageStruct> {

        override fun parse(response: String): ImageStruct {
            try {
                val imageStruct = ImageStruct()
                val rootJsonObject = JSONObject(response)
                    .getJSONArray("response")
                    .getJSONObject(0)
                imageStruct.ownerId = rootJsonObject.getString("owner_id").toInt()
                imageStruct.mediaId = rootJsonObject.getString("id").toInt()
                return imageStruct;

            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }

        }
    }
}