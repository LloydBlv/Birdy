package com.flydroid.birdy.domain

import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LastLocationRequest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

data class LastLocationParams(
    val priority: Priority = Priority.BALANCED,
    val maxUpdateAge: Duration = 10.hours,
    val minUpdateInterval: Duration = 30.seconds,
    val stopAfter: Duration = 1.hours,
    val granularity: Granularity = Granularity.PERMISSION_LEVEL,
)

fun LastLocationParams.toCurrentLocationRequest(): CurrentLocationRequest {
    return CurrentLocationRequest.Builder()
        .setPriority(priority.toLocationRequestPriority())
        .setDurationMillis(stopAfter.inWholeMilliseconds)
        .setMaxUpdateAgeMillis(maxUpdateAge.inWholeMilliseconds)
        .setGranularity(granularity.toCurrentLocationGranularity())
        .build()
}

fun LastLocationParams.toLastLocationRequest(): LastLocationRequest {
    return LastLocationRequest.Builder()
        .setMaxUpdateAgeMillis(maxUpdateAge.inWholeMilliseconds)
        .setGranularity(granularity.toCurrentLocationGranularity())
        .build()
}