package com.flydroid.birdy.domain

import com.google.android.gms.location.Priority

enum class Priority {
    HIGH,
    BALANCED,
    LOW;

    fun toLocationRequestPriority(): Int {
        return when (this) {
            HIGH -> Priority.PRIORITY_HIGH_ACCURACY
            BALANCED -> Priority.PRIORITY_BALANCED_POWER_ACCURACY
            LOW -> Priority.PRIORITY_LOW_POWER
        }
    }
}
