package com.isanechek.extractx.core.data.youtube.dto

data class YoutubeItem(val id: Int,
                       val vId: String,
                       val defaultThumbnail: String,
                       val channelUrl: String,
                       val date: String,
                       val author: String,
                       val channelTitle: String,
                       val title: String,
                       val url: String,
                       val lengthSeconds: String,
                       val qualities: List<Quality>,
                       val thumbnails: List<Thumbnail>)