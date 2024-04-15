package com.flydroid.birdy.domain

import com.google.android.gms.location.LocationRequest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

data class ObserveParams(
    val priority: Priority = Priority.BALANCED,
    val updateInterval: Duration = 60.seconds,
    val minUpdateInterval: Duration = 30.seconds,
    val stopAfter: Duration = 1.hours,
)

fun ObserveParams.toLocationRequest(): LocationRequest {
    return LocationRequest.Builder(updateInterval.inWholeMilliseconds)
        .setPriority(priority.toLocationRequestPriority())
        .setMinUpdateIntervalMillis(minUpdateInterval.inWholeMilliseconds)
        .setDurationMillis(stopAfter.inWholeMilliseconds)
        .build()
}

