package com.isanechek.extractx.core.data.youtube.parser

import android.text.Html
import com.isanechek.extractx.core.common.DebugUtilsContract
import com.isanechek.extractx.core.data.youtube.YoutubeParserContract
import com.isanechek.extractx.core.data.youtube.dto.Quality
import com.isanechek.extractx.core.data.youtube.dto.Thumbnail
import com.isanechek.extractx.core.data.youtube.dto.YoutubeItem
import com.isanechek.extractx.core.extension.asLog
import com.isanechek.extractx.core.extension.decode
import com.isanechek.extractx.core.extension.emptyString
import org.json.JSONObject
import java.net.URLDecoder
import java.util.regex.Pattern

class YoutubeParser(private val debug: DebugUtilsContract) : YoutubeParserContract {

    override suspend fun getVideoId(youtubeUrl: String): String {
        val patterns = arrayOf(
            "v=([a-zA-Z0-9_\\-]*)",
            "v/([a-zA-Z0-9_\\-]*)",
            "youtu.be/([a-zA-Z0-9_\\-]*)",
            "vnd.youtube:([a-zA-Z0-9_\\-]*)",
            "embed/([a-zA-Z0-9_\\-]*)")

        for (pattern in patterns) {
            val m = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(youtubeUrl)
            if (m.find()) return URL_GET_VIDEO_INFO + m.group(1)
        }

        return emptyString
    }

    /**
     *  Callback return or result, or error message
     */
    private fun parseVideoInfo(source: String, callback: (List<Thumbnail>, String) -> Unit) {

        if (source.isEmpty()) {
            callback.invoke(emptyList(), "Error! Response info empty!")
            return
        }

        val rootJo = JSONObject(source)
        rootJo.toString().asLog(tag = "parserVideoInfo")
       val playabilityStatus = rootJo.getJSONObject("playabilityStatus")
        when (playabilityStatus.getString("status")) {
            "ERROR" -> {
                val errorMessage = playabilityStatus.getString("reason")
                errorMessage.asLog(tag = "parseVideoInfo", isError = true)
                callback.invoke(emptyList(), errorMessage)
            }
            "OK" -> {
                val cache = mutableListOf<Thumbnail>()
                val ja = JSONObject(source)
                    .getJSONObject("videoDetails")
                    .getJSONObject("thumbnail")
                    .getJSONArray("thumbnails")

                for (i in 0 until ja.length()) {
                    val jo = ja[i] as JSONObject
                    cache.add(Thumbnail(
                        url = jo.getString("url"),
                        width = jo.getInt("width"),
                        height = jo.getInt("height")))
                }

                "cache size ${cache.size}".asLog(tag = "parserVideoInfo")
                callback.invoke(cache, emptyString)
            }
        }
    }

    private fun getUrlFromParams(params: String): String {
        val m = Pattern.compile("(\\w+)=([^&]*)").matcher(params)
        var sig = ""
        var quality = ""
        var url = ""
        while (m.find()) {
            val name = m.group(1)
            if (url.isEmpty() && "url".equals(name, ignoreCase = true)) {
                url = m.group(2)
            } else if (sig.isEmpty() && "sig".equals(name, ignoreCase = true)) {
                sig = m.group(2)
            } else if (quality.isEmpty() && "quality".equals(name, ignoreCase = true)) {
                quality = m.group(2)
            }

            if (url.isNotEmpty() && sig.isNotEmpty() && quality.isNotEmpty()) {
                break
            }
        }

        return """$url&signature=$sig"""
    }

    private fun indexOf(needle: Int, haystack: Array<Int>): Int {
        for (i in 0 until haystack.size) {
            if (haystack[i] == needle) return i
        }
        return -1
    }



    override suspend fun parse(document: String): YoutubeItem {
        val items = mutableListOf<Quality>()

        val m = Pattern.compile("([^&=]*)=([^$&]*)", Pattern.CASE_INSENSITIVE).matcher(document)
        var title = emptyString
        var fmtList = emptyString
        var url_encoded_fmt_stream_map = emptyString
        var error = emptyString
        var length_seconds = emptyString
        var thumbnail_url = emptyString
        var timestamp = emptyString
        var video_id = emptyString
        var baseUrl = emptyString
        var author = emptyString
        var player_response = emptyString

        loop@ while (m.find()) {
            val name = m.group(1)
            when {
                fmtList.isEmpty() && "fmt_list".equals(name, ignoreCase = true) -> fmtList = m.group(2).decode()
                url_encoded_fmt_stream_map.isEmpty() && "url_encoded_fmt_stream_map".equals(name, ignoreCase = true) -> url_encoded_fmt_stream_map = m.group(2).decode()
                title.isEmpty() && "title".equals(name, ignoreCase = true) -> title = m.group(2).decode()
                error.isEmpty() && "reason".equals(name, ignoreCase = true) -> error = Html.fromHtml(m.group(2).decode()).toString()
                length_seconds.isEmpty() && "length_seconds".equals(name, ignoreCase = true) -> length_seconds = m.group(2).decode()
                thumbnail_url.isEmpty() && "thumbnail_url".equals(name, ignoreCase = true) -> thumbnail_url = m.group(2).decode()
                timestamp.isEmpty() && "timestamp".equals(name, ignoreCase = true) -> timestamp = m.group(2).decode()
                video_id.isEmpty() && "video_id".equals(name, ignoreCase = true) -> video_id = m.group(2).decode()
                baseUrl.isEmpty() && "baseUrl".equals(name, ignoreCase = true) -> baseUrl = m.group(2).decode()
                author.isEmpty() && "author".equals(name, ignoreCase = true) -> author = m.group(2).decode()
                player_response.isEmpty() && "player_response".equals(name, ignoreCase = true) -> player_response = m.group(2).decode()
            }
            when {
                fmtList.isNotEmpty()
                        && url_encoded_fmt_stream_map.isNotEmpty()
                        && title.isNotEmpty()
                        && error.isNotEmpty()
                        && baseUrl.isNotEmpty()
                        && author.isNotEmpty()
                        && length_seconds.isNotEmpty()
                        && player_response.isNotEmpty()
                        && thumbnail_url.isNotEmpty()
                        && video_id.isNotEmpty()
                        && timestamp.isNotEmpty() -> break@loop
            }
        }

        if (fmtList.isNotEmpty() && url_encoded_fmt_stream_map.isNotEmpty()) {
            val fmtMatcher = Pattern.compile("(\\d+)\\/(\\d+x\\d+)").matcher(fmtList)
            val streamStrs = url_encoded_fmt_stream_map.split(",")
            var fmtIndex = 0
            while (fmtMatcher.find()) {
                fmtIndex.unaryPlus()
                if (streamStrs.size == fmtIndex) {
                    break
                }

                val ind = indexOf(fmtMatcher.group(1).toInt(), YouTubeFMTQuality.supported)
                if (ind == -1) continue
                val height = YouTubeFMTQuality.supported_titles[ind]
                val fileName = URLDecoder.decode(getUrlFromParams(streamStrs[fmtIndex]), "UTF-8")
                items.add(Quality(height, fileName, false))
            }
        }

        /**
         *  Need implementation handle error
         */
        var thumbnailsResult = listOf<Thumbnail>()
        parseVideoInfo(player_response) { result, errorMessage ->
            thumbnailsResult = result
        }

        return YoutubeItem(
            id = video_id.hashCode(),
            vId = video_id,
            channelTitle = "",
            channelUrl = baseUrl,
            date = timestamp,
            defaultThumbnail = thumbnail_url,
            qualities = items,
            title = title,
            url = "",
            author = author,
            lengthSeconds = length_seconds,
            thumbnails = thumbnailsResult)

    }

    companion object {
        const val URL_GET_VIDEO_INFO = "https://www.youtube.com/get_video_info?&video_id="
    }

    object YouTubeFMTQuality {

        const val GPP3_LOW = 13        //3GPP (MPEG-4 encoded) Low quality
        const val GPP3_MEDIUM = 17        //3GPP (MPEG-4 encoded) Medium quality
        const val MP4_NORMAL = 18        //MP4  (H.264 encoded) Normal quality
        const val MP4_HIGH = 22        //MP4  (H.264 encoded) High quality
        const val MP4_HIGH1 = 37        //MP4  (H.264 encoded) High quality

        const val GPP3_LOW_TITLE: String = "240p"        //3GPP (MPEG-4 encoded) Low quality
        const val GPP3_MEDIUM_TITLE: String = "360p"        //3GPP (MPEG-4 encoded) Medium quality
        const val MP4_NORMAL_TITLE: String = "480p"        //MP4  (H.264 encoded) Normal quality
        const val MP4_HIGH_TITLE: String = "720p HD"        //MP4  (H.264 encoded) High quality
        const val MP4_HIGH1_TITLE: String = "1080p HD"        //MP4  (H.264 encoded) High quality

        val supported = arrayOf(GPP3_LOW, GPP3_MEDIUM, MP4_NORMAL, MP4_HIGH, MP4_HIGH1)

        val supported_titles = arrayOf(GPP3_LOW_TITLE, GPP3_MEDIUM_TITLE, MP4_NORMAL_TITLE, MP4_HIGH_TITLE, MP4_HIGH1_TITLE)

    }

}