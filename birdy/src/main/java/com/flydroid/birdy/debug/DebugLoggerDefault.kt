package com.flydroid.birdy.debug

import android.util.Log

private const val TAG = "Birdy"
class DebugLoggerDefault(private val debugEnabled: Boolean): DebugLogger {
    override fun log(message: String, level: Int) {
        if(!debugEnabled) return
        when(level) {
            Log.DEBUG -> Log.d(TAG, message)
            Log.INFO -> Log.i(TAG, message)
            Log.WARN -> Log.w(TAG, message)
            Log.ERROR -> Log.e(TAG, message)
        }
    }
    override fun logException(exception: Throwable) {
        if(!debugEnabled) return
        Log.e(TAG, "Exception: ${exception.message}", exception)
    }

}