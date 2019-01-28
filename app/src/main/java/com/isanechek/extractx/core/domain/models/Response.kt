package com.isanechek.extractx.core.domain.models

sealed class Response {
    data class YoutubeResult(val content: String) : Response()
    object NetworkError : Response()
    object RequestError : Response()
    class ServerError(val errorCode: Int) : Response()
    object Empty : Response()
}