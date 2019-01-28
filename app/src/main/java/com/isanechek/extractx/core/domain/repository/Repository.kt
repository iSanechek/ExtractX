package com.isanechek.extractx.core.domain.repository

import androidx.lifecycle.LiveData
import com.isanechek.extractx.core.domain.models.Request
import com.isanechek.extractx.core.domain.models.Resource

interface Repository<T : Any> {
    fun resource(): LiveData<Resource<T>>
    suspend fun loadData(contentUrl: String)
}