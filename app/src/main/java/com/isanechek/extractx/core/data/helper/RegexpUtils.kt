package com.isanechek.extractx.core.data.helper

import com.isanechek.extractx.core.extension.emptyString
import java.util.regex.Pattern

object RegexpUtils {

    fun getYoutubeVideoId(videoUrl: String): String {
        val patterns = arrayOf(
            "v=([a-zA-Z0-9_\\-]*)",
            "v/([a-zA-Z0-9_\\-]*)",
            "youtu.be/([a-zA-Z0-9_\\-]*)",
            "vnd.youtube:([a-zA-Z0-9_\\-]*)",
            "embed/([a-zA-Z0-9_\\-]*)")

        for (pattern in patterns) {
            val m = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(videoUrl)
            if (m.find()) return "https://www.youtube.com/get_video_info?&video_id="+ m.group(1)
        }
        return emptyString
    }
}