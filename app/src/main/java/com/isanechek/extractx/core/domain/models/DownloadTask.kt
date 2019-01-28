package com.isanechek.extractx.core.domain.models

data class DownloadTask(val id: String, val title: String, val cover: String) {

    companion object {
        const val DB_TABLE_NAME = "extractx_download_task"
        const val ID = "_id"
        const val TITLE = "_title"
        const val COVER_URL = "_cover_url"
    }
}