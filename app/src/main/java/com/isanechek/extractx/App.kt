package com.isanechek.extractx

import android.app.Application
import com.huxq17.download.DownloadConfig
import com.isanechek.extractx.core.di.coreModule
import com.isanechek.extractx.core.di.dashboardModule
import com.isanechek.extractx.core.di.dataModule
import com.isanechek.extractx.core.di.domainModule
import org.koin.android.ext.android.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(
            coreModule,
            dashboardModule,
            dataModule,
            domainModule))

        DownloadConfig.newBuilder()
            .setThreadNum(3)
            .setMaxRunningTaskNum(3)
            .setForceReDownload(true)
            .build()
    }
}