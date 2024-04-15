package com.flydroid.birdy

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    fun parseISODateToEpochMillis(dateStr: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return dateFormat.parse(dateStr)?.time ?: throw IllegalArgumentException("Invalid date format")
    }
}