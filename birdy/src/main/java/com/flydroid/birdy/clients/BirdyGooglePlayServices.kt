package com.flydroid.birdy.clients

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.flydroid.birdy.debug.DebugLogger
import com.flydroid.birdy.debug.DebugLoggerDefault
import com.flydroid.birdy.domain.ObserveParams
import com.flydroid.birdy.domain.OneshotRequest
import com.flydroid.birdy.extensions.hasCoarseLocationPermission
import com.flydroid.birdy.extensions.hasFineLocationPermission
import com.flydroid.birdy.location.LocationProvider
import com.flydroid.birdy.location.LocationProviderDefault
import com.flydroid.birdy.sync.LocationSyncer
import com.flydroid.birdy.sync.LocationSyncerDefault
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class BirdyGooglePlayServices(
    private val appContext: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    debugMode: Boolean = false,
    private val debugLogger: DebugLogger = DebugLoggerDefault(debugMode),
    private val locationProvider: LocationProvider = LocationProviderDefault(
        appContext,
        debugLogger
    ),
    apiKey: String,
    private val locationSyncer: LocationSyncer = LocationSyncerDefault(appContext, debugLogger, apiKey = apiKey)
) : BirdyClient {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val handler = CoroutineExceptionHandler { _, exception ->
        debugLogger.logException(exception)
    }

    init {
        locationProvider.setLocationReceivedListener { latitude, longitude ->
            CoroutineScope(ioDispatcher + handler).launch {
                locationSyncer.syncLocation(latitude, longitude)
            }
        }
    }

    override fun startLocationUpdates(request: ObserveParams) {
        when {
            !hasLocationPermission() -> debugLogger.log("Location permission is not granted", Log.ERROR)
            locationProvider.isObserving -> debugLogger.log("Location updates already started")
            else -> locationProvider.requestUpdates(request)
        }
    }

    override fun stopLocationUpdates() {
        debugLogger.log("stopLocationUpdates")
        locationProvider.removeUpdates()

    }

    @SuppressLint("MissingPermission")
    override fun requestSingleUpdate(
        request: OneshotRequest
    ) {
        debugLogger.log("requestSingleUpdate")
        when {
            !hasLocationPermission() -> debugLogger.log("Location permission is not granted", Log.ERROR)
            request is OneshotRequest.LastKnownLocation -> locationProvider.requestLastLocation(request)
            request is OneshotRequest.FreshLocation -> locationProvider.requestCurrentLocation(request)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return appContext.hasFineLocationPermission()
                || appContext.hasCoarseLocationPermission()
    }
}