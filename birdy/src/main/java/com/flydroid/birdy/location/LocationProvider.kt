package com.flydroid.birdy.location

import com.flydroid.birdy.domain.ObserveParams
import com.flydroid.birdy.domain.OneshotRequest

interface LocationProvider {
    val isObserving: Boolean

    fun requestUpdates(params: ObserveParams)
    fun removeUpdates()

    fun requestLastLocation(request: OneshotRequest.LastKnownLocation)
    fun requestCurrentLocation(request: OneshotRequest.FreshLocation)

    fun setLocationReceivedListener(listener: OnLocationReceivedListener)

    fun interface OnLocationReceivedListener {
        fun onLocationReceived(
            latitude: Double,
            longitude: Double
        )
    }
}