package com.isanechek.extractx.core.data.network

import com.isanechek.extractx.core.common.DebugUtilsContract
import com.isanechek.extractx.core.domain.models.Request
import com.isanechek.extractx.core.domain.models.Response
import com.isanechek.extractx.core.extension.emptyString
import okhttp3.OkHttpClient

class WorkerHelper(
    private val debug: DebugUtilsContract
) : WorkerHelperContract {
    override fun fetchFromServer(request: Request): Response {
        val client = OkHttpClient()
        return if (request is Request.Youtube) {
            val requestUrl = request.url
            debug.log("$TAG Youtube request $requestUrl")
            val okRequest = okhttp3.Request.Builder()
                .url(requestUrl)
                .build()
            val response = client.newCall(okRequest).execute()
            if (response.isSuccessful) {
                val result = response.body()?.string() ?: emptyString
                if (result.isNotEmpty()) {
                    Response.YoutubeResult(result)
                } else Response.Empty
            } else Response.ServerError(response.code())
        } else Response.RequestError
    }

    companion object {
        private const val TAG = "WorkerHelper"
    }
}