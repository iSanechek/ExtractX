package com.isanechek.extractx.core.common

import java.text.DecimalFormat

object Utils {
    fun getDataSize(size: Long): String {
        val format = DecimalFormat("####.00")
        return when {
            size < 1024 -> "${size}bytes"
            size < 1024 * 1024 -> format.format(size / 1024f) + "KB"
            size < 1024 * 1024 * 1024 -> format.format(size / 1024f / 1024f) + "MB"
            size < 1024 * 1024 * 1024 * 1024 -> format.format(size / 1024f / 1024f / 1024f) + "GB"
            else -> "size: error"
        }
    }
}