package com.isanechek.extractx.core.di

import com.isanechek.extractx.core.common.DebugUtilsContract
import com.isanechek.extractx.core.platform.DebugUtils
import org.koin.dsl.module.module

val coreModule = module {

    /**
     * Create DebugUtils instance
     */
    single<DebugUtilsContract> {
        DebugUtils()
    }
}