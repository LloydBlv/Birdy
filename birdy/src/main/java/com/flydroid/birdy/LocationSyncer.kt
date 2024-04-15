package com.flydroid.birdy

interface LocationSyncer {
    fun syncLocation(latitude: Double, longitude: Double)
}