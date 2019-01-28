package com.isanechek.extractx.core.data.network

import com.isanechek.extractx.core.domain.models.Request
import com.isanechek.extractx.core.domain.models.Response

interface WorkerHelperContract {
    fun fetchFromServer(request: Request): Response
}