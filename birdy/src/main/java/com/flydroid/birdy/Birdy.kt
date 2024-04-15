package com.flydroid.birdy

import android.content.Context
import com.flydroid.birdy.clients.BirdyClient
import com.flydroid.birdy.clients.BirdyGooglePlayServices
import com.flydroid.birdy.debug.DebugLogger
import com.flydroid.birdy.debug.DebugLoggerDefault
import com.flydroid.birdy.domain.ObserveParams
import com.flydroid.birdy.domain.OneshotRequest

object Birdy {
    private var birdy: BirdyClient? = null

    fun init(
        context: Context,
        debugMode: Boolean,
        apiKey: String,
        debugLogger: DebugLogger = DebugLoggerDefault(debugMode)
    ) {
        birdy = BirdyGooglePlayServices(
            appContext = context.applicationContext,
            debugMode = debugMode,
            apiKey = apiKey,
            debugLogger = debugLogger
        )
    }

    fun startLocationUpdates(request: ObserveParams) {
        if (birdy == null) {
            println("Birdy is not initialized")
            return
        }
        birdy?.startLocationUpdates(request)
    }

    fun stopLocationUpdates() {
        if (birdy == null) {
            println("Birdy is not initialized")
            return
        }
        birdy?.stopLocationUpdates()
    }

    fun requestSingleUpdate(request: OneshotRequest) {
        if (birdy == null) {
            println("Birdy is not initialized")
            return
        }
        birdy?.requestSingleUpdate(request)
    }

}