package com.isanechek.extractx.core.domain.repository.youtube

import com.isanechek.extractx.core.common.Mapper
import com.isanechek.extractx.core.data.youtube.dto.YoutubeItem
import com.isanechek.extractx.core.domain.models.DashboardModel
import com.isanechek.extractx.core.domain.models.QualityModel
import com.isanechek.extractx.core.domain.models.YoutubeModel

class YoutubeMapper : Mapper<DashboardModel, YoutubeItem> {

    override fun map(from: YoutubeItem): DashboardModel = with(from) {
        YoutubeModel(
            id = id,
            vId = vId,
            author = author,
            channelTitle = channelTitle,
            channelUrl = channelUrl,
            date = date,
            lengthSeconds = lengthSeconds,
            maxThumbnail = thumbnails.lastOrNull()?.url,
            minThumbnail = thumbnails.firstOrNull()?.url,
            qualities = qualities.map { QualityModel(
                height = it.height,
                fileName = it.fileName,
                hd = it.hd
            ) }.toList(),
            title = title,
            url = url
        )
    }

    override fun map(from: List<YoutubeItem>): List<DashboardModel> = from.map { map(it) }.toList()

}