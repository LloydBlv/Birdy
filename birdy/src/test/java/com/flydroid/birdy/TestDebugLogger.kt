package com.flydroid.birdy

import com.flydroid.birdy.debug.DebugLogger

class TestDebugLogger: DebugLogger {
    override fun log(message: String, level: Int) {
        println("message = $message, level = $level")
    }

    override fun logException(exception: Throwable) {
        println("exception = $exception")
    }
}