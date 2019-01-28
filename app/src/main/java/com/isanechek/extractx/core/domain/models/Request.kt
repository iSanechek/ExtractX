package com.isanechek.extractx.core.domain.models

sealed class Request {
    data class Youtube(val url: String): Request()
}