package com.isanechek.extractx.core.platform

import android.util.Log
import com.isanechek.extractx.core.common.DebugUtilsContract

class DebugUtils : DebugUtilsContract {
    override fun log(logMessage: String?) {
        Log.d(TAG, logMessage)
    }

    companion object {
        private const val TAG = "ExtractX"
    }

}