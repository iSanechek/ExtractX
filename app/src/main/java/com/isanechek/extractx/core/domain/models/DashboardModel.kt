package com.isanechek.extractx.core.domain.models

sealed class DashboardModel

data class QualityModel(val height: String, val fileName: String, val hd: Boolean)

data class YoutubeModel(val id: Int,
                        val vId: String,
                        val minThumbnail: String?,
                        val maxThumbnail: String?,
                        val channelUrl: String,
                        val date: String,
                        val author: String,
                        val channelTitle: String,
                        val title: String,
                        val url: String,
                        val lengthSeconds: String,
                        val qualities: List<QualityModel>) : DashboardModel()