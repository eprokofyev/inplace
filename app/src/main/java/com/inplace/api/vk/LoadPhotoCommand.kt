package com.inplace.api.vk

import android.net.Uri
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKHttpPostCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class LoadPhotoCommand(private val uri: Uri): ApiCommand<FileUploadInfo>() {
    override fun onExecute(manager: VKApiManager): FileUploadInfo {

        val fileUploadCall = VKHttpPostCall.Builder()
            .url(VkSingleton.UploadServer.uploadUrl)
            .args("photo", uri, "image.jpg")
            .timeout(TimeUnit.MINUTES.toMillis(5))
            .retryCount(3)
            .build()
        return manager.execute(fileUploadCall, null, ResponseApiParser())
    }


    private class ResponseApiParser : VKApiResponseParser<FileUploadInfo> {

        override fun parse(response: String): FileUploadInfo {
            try {
                val rootJsonObject = JSONObject(response)
                val fileUploadInfo = FileUploadInfo()
                fileUploadInfo.server = rootJsonObject.getString("server")
                fileUploadInfo.photo = rootJsonObject.getString("photo")
                fileUploadInfo.hash = rootJsonObject.getString("hash")
                return fileUploadInfo;

            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }

        }
    }
}