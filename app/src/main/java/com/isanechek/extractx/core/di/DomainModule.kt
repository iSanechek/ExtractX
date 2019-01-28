package com.isanechek.extractx.core.di

import com.isanechek.extractx.core.common.Mapper
import com.isanechek.extractx.core.data.youtube.dto.YoutubeItem
import com.isanechek.extractx.core.domain.models.DashboardModel
import com.isanechek.extractx.core.domain.repository.Repository
import com.isanechek.extractx.core.domain.repository.youtube.YoutubeMapper
import com.isanechek.extractx.core.domain.repository.youtube.YoutubeRepository
import org.koin.dsl.module.module

val domainModule = module {

    factory<Mapper<DashboardModel, YoutubeItem>>(name = YOUTUBE_MAPPER) {
        YoutubeMapper()
    }

    factory<Repository<DashboardModel>>(name = YOUTUBE_REPOSITORY) {
        YoutubeRepository(
            get(),
            get(),
            get(),
            get(name = YOUTUBE_MAPPER))
    }
}

const val YOUTUBE_MAPPER = "youtube.mapper"
const val YOUTUBE_REPOSITORY = "youtube.repository"