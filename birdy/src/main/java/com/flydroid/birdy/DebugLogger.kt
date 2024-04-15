package com.flydroid.birdy

import android.util.Log

interface DebugLogger {
    fun log(message: String, level: Int = Log.DEBUG)
    fun logException(exception: Throwable)
}