package com.isanechek.extractx.core.data.down

import android.net.Uri

sealed class DownloadResponse {
    data class Done(val localUri: Uri) : DownloadResponse()
    data class Load(val downloadId: Long) : DownloadResponse()
    data class Error(val errorMessage: String) : DownloadResponse()
}

