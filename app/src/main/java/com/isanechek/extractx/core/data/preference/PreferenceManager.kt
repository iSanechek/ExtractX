package com.isanechek.extractx.core.data.preference

import android.content.SharedPreferences
import androidx.core.content.edit
import com.isanechek.extractx.core.extension.emptyString

class PreferenceManager(
    private val preferences: SharedPreferences
) : PreferenceContract {

    override var qualityRemember: Boolean
        get() = preferences.getBoolean("quality_remember", false)
        set(value) {
            preferences.edit {
                putBoolean("quality_remember", value)
            }
        }

    override var quality: String
        get() = preferences.getString("quality_chose", emptyString) ?: emptyString
        set(value) {
            preferences.edit {
                putString("quality_chose", value)
            }
        }

}