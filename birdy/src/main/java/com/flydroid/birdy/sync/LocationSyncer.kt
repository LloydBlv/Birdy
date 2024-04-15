package com.flydroid.birdy.sync

interface LocationSyncer {
    fun syncLocation(latitude: Double, longitude: Double)
}