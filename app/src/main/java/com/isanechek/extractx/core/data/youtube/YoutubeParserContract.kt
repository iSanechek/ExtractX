package com.isanechek.extractx.core.data.youtube

import com.isanechek.extractx.core.data.youtube.dto.YoutubeItem

interface YoutubeParserContract {
    suspend fun getVideoId(youtubeUrl: String): String
    suspend fun parse(document: String): YoutubeItem
}