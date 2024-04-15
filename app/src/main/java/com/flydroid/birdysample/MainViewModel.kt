package com.flydroid.birdysample

import androidx.lifecycle.ViewModel
import com.flydroid.birdy.debug.DebugLogger
import com.flydroid.birdy.debug.DebugLoggerDefault
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface Log {
    data class Message(val message: String, val level: Int) : Log
    data class Exception(val exception: Throwable) : Log
}

class MainViewModel : ViewModel(), DebugLogger {
    private val logcatDebugLogger = DebugLoggerDefault(debugEnabled = true)

    private val _mutableState = MutableStateFlow(emptyList<Log>())
    val state: StateFlow<List<Log>>
        get() = _mutableState.asStateFlow()

    override fun log(message: String, level: Int) {
        logcatDebugLogger.log(message, level)
        _mutableState.update { it + Log.Message(message, level) }
    }

    override fun logException(exception: Throwable) {
        logcatDebugLogger.logException(exception)
        _mutableState.update { it + Log.Exception(exception) }
    }

    fun clearLogs() {
        _mutableState.update { emptyList() }
    }
}