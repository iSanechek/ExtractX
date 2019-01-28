package com.isanechek.extractx.core.di

import com.isanechek.extractx.core.data.database.DatabaseHandler
import com.isanechek.extractx.core.data.down.DownloadService
import com.isanechek.extractx.core.data.down.DownloadServiceContract
import com.isanechek.extractx.core.data.network.WorkerHelper
import com.isanechek.extractx.core.data.network.WorkerHelperContract
import com.isanechek.extractx.core.data.youtube.YoutubeParserContract
import com.isanechek.extractx.core.data.youtube.parser.YoutubeParser
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val dataModule = module {

    /**
     * Create Youtube parser instance
     * @params DebugUtilsContract
     */
    factory<YoutubeParserContract> {
        YoutubeParser(get())
    }

    single<WorkerHelperContract> {
        WorkerHelper(get())
    }

    single<DownloadServiceContract> {
        DownloadService(androidContext())
    }

    single {
        DatabaseHandler(androidContext())
    }
}