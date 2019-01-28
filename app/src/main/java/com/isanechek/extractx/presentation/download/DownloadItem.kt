package com.isanechek.extractx.presentation.download

import com.huxq17.download.DownloadInfo

data class DownloadItem(val id: String, val title: String, val coverUrl: String, val info: DownloadInfo)