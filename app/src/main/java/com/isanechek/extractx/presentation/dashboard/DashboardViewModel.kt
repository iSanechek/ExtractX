package com.isanechek.extractx.presentation.dashboard

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.huxq17.download.Pump
import com.isanechek.extractx.core.data.database.DatabaseHandler
import com.isanechek.extractx.core.data.down.DownloadResponse
import com.isanechek.extractx.core.data.down.DownloadServiceContract
import com.isanechek.extractx.core.data.youtube.parser.YoutubeParser
import com.isanechek.extractx.core.domain.models.DashboardModel
import com.isanechek.extractx.core.domain.models.DownloadTask
import com.isanechek.extractx.core.domain.models.YoutubeModel
import com.isanechek.extractx.core.domain.repository.Repository
import com.isanechek.extractx.core.extension.asLog
import com.isanechek.extractx.core.extension.emptyString
import com.isanechek.extractx.core.platform.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.regex.Pattern

class DashboardViewModel(
    private val repository: Repository<DashboardModel>,
    private val downloadingService: DownloadServiceContract,
    private val database: DatabaseHandler
) : BaseViewModel() {
    val response = repository.resource()

    private fun getVideoId(youtubeUrl: String): String {
        val patterns = arrayOf(
            "v=([a-zA-Z0-9_\\-]*)",
            "v/([a-zA-Z0-9_\\-]*)",
            "youtu.be/([a-zA-Z0-9_\\-]*)",
            "vnd.youtube:([a-zA-Z0-9_\\-]*)",
            "embed/([a-zA-Z0-9_\\-]*)")

        for (pattern in patterns) {
            val m = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(youtubeUrl)
            if (m.find()) return YoutubeParser.URL_GET_VIDEO_INFO + m.group(1)
        }

        return emptyString
    }

    fun loadInfo(url: String) = launch {
        repository.loadData(url)
    }

    fun cancelRequest() {
        if (job.isActive) {
            job.cancel()
        }
    }

    val downloadState = MutableLiveData<Long>()
    fun loadVideo(videoUrl: String, fileName: String) = launch {
        videoUrl.asLog(tag = "DashboardViewModel")
        downloadingService.download(requestUrl = videoUrl, fileName = fileName, title = "ExtractX") { callback ->
            when (callback) {
                is DownloadResponse.Load -> {
                    downloadState.value = callback.downloadId
                }
                is DownloadResponse.Error -> {}
                is DownloadResponse.Done -> {

                }
            }
        }
    }

    fun insertTask(model: YoutubeModel) = launch(Dispatchers.IO) {
        val _id_ = getVideoId(model.url)
        "video url ${model.url}".asLog()
        "video id $_id_".asLog()
        val task = DownloadTask(
            id = _id_,
            title = model.title,
            cover = model.minThumbnail!!
        )
        database.insertTask(task)
    }

    fun cancelDownload(downloadId: Long) {
        downloadingService.removeDownloads(downloadId)
    }

    val setupActionBtn = MutableLiveData<Int>()

    fun setInfo(index: Int) {
        setupActionBtn.value = index
    }

    fun downloadVideo(videoUrl: String) {
        Pump.download(videoUrl, File(Environment.DIRECTORY_MOVIES, "test_video.mp4").absolutePath)
    }
}