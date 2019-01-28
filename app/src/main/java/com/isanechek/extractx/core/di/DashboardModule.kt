package com.isanechek.extractx.core.di

import com.isanechek.extractx.presentation.dashboard.DashboardViewModel
import com.isanechek.extractx.presentation.download.DownloadViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val dashboardModule = module {

    viewModel {
        DashboardViewModel(get(name = YOUTUBE_REPOSITORY), get(), get())
    }

    viewModel {
        DownloadViewModel(get())
    }
}