package com.isanechek.extractx.presentation.download

import com.huxq17.download.DownloadInfo
import com.huxq17.download.Pump
import com.isanechek.extractx.core.data.database.DatabaseHandler
import com.isanechek.extractx.core.data.helper.RegexpUtils
import com.isanechek.extractx.core.domain.models.DownloadTask
import com.isanechek.extractx.core.extension.asLog
import com.isanechek.extractx.core.platform.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadViewModel(private val database: DatabaseHandler) : BaseViewModel() {

    private val cache = mutableListOf<DownloadTask>()

    init {

    }

    private fun test() = launch(Dispatchers.IO) {
        val tasks = database.getAllTasks()
        withContext(Dispatchers.Main) {
            "Tasks from db ${tasks.size}".asLog(tag = TAG)
            if (tasks.isNotEmpty()) {
                cache.addAll(tasks)
            }
        }
    }

    fun mapInfo(info: DownloadInfo): DownloadItem? {
        val requestUrl = info.url
        "mapInfo Request Url $requestUrl".asLog(tag = TAG)
        val videoId = RegexpUtils.getYoutubeVideoId(requestUrl)
        "mapInfo video id $videoId".asLog(tag = TAG)
        "mapInfo cache size ${cache.size}".asLog(tag = TAG)
        val task = cache.firstOrNull { it.id == videoId } ?: return null
        return DownloadItem(
            id = task.id,
            title = task.title,
            coverUrl = task.cover,
            info = info
        )
    }

    fun mapAllTask2(): List<DownloadItem> = Pump.getAllDownloadList().map { info ->
        val _id_ = RegexpUtils.getYoutubeVideoId(info.url)
        val task = cache.find { it.id == _id_ }!!
        DownloadItem(
            id = task.id,
            coverUrl = task.cover,
            title = task.title,
            info = info
        )
    }.toList()

    fun mapAllTask(): List<DownloadItem> {
        test()
        val temp = mutableListOf<DownloadItem>()
        "mapAllTask cache size ${cache.size}".asLog(tag = TAG)
        for (info in Pump.getDownloadingList()) {
            val requestUrl = info.url
            "mapAllTask Request Url $requestUrl".asLog(tag = TAG)
            val videoId = RegexpUtils.getYoutubeVideoId(requestUrl)
            "mapAllTask video id $videoId".asLog(tag = TAG)
            val task = cache.firstOrNull { it.id == videoId }
            if (task != null) {
                temp.add(
                    DownloadItem(
                        id = task.id,
                        title = task.title,
                        coverUrl = task.cover,
                        info = info
                    )
                )
            }
        }
        "mapAllTask cache size ${temp.size}".asLog(tag = TAG)
        return temp
    }



    companion object {
        private const val TAG = "DownloadViewModel"
    }
}