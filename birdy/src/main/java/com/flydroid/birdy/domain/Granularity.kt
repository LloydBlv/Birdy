package com.flydroid.birdy.domain

enum class Granularity {
    COARSE,
    FINE,
    PERMISSION_LEVEL
}

fun Granularity.toCurrentLocationGranularity(): Int {
    return when (this) {
        Granularity.COARSE -> com.google.android.gms.location.Granularity.GRANULARITY_COARSE
        Granularity.FINE -> com.google.android.gms.location.Granularity.GRANULARITY_FINE
        Granularity.PERMISSION_LEVEL -> com.google.android.gms.location.Granularity.GRANULARITY_PERMISSION_LEVEL
    }
}