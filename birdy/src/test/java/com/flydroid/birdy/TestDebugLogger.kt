package com.flydroid.birdy

class TestDebugLogger: DebugLogger {
    override fun log(message: String, level: Int) {
        println("message = $message, level = $level")
    }

    override fun logException(exception: Throwable) {
        println("exception = $exception")
    }
}