package com.isanechek.extractx.core.domain.models

class QualityState(val qualityRemember: Boolean, val network: Int) {
    companion object {
        const val WIFI = 0
        const val NETWORK_4_G = 1
        const val NETWORK_3_G = 2
        const val NETWORK_EDGE = 3
    }
}