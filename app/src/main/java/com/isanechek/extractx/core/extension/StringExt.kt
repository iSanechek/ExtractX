package com.isanechek.extractx.core.extension

import android.util.Log
import com.isanechek.extractx.BuildConfig
import com.isanechek.extractx.core.data.youtube.parser.YoutubeParser
import java.net.URLDecoder
import java.util.regex.Pattern

fun String.Companion.empty() = ""

fun String.decode(): String = URLDecoder.decode(this, "UTF-8")

const val emptyString = ""

fun String.asLog(tag: String? = null, isError: Boolean = false) {

    if (BuildConfig.DEBUG) {
        val appName = "ExtractX"
        val defaultTag: String = when (tag) {
            null -> appName
            else -> "$appName $tag"
        }
        if (isError) Log.e(defaultTag, this) else Log.d(defaultTag, this)
    }
}

