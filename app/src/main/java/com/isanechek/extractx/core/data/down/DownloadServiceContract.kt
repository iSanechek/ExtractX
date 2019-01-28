package com.isanechek.extractx.core.data.down

interface DownloadServiceContract {
    fun download(requestUrl: String, title: String, fileName: String, callback: (DownloadResponse) -> Unit)
    fun removeDownloads(vararg ids: Long)
}