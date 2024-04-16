package com.flydroid.birdy.clients

import com.flydroid.birdy.domain.ObserveParams
import com.flydroid.birdy.domain.OneshotRequest

internal interface BirdyClient {
    fun startLocationUpdates(request: ObserveParams)
    fun stopLocationUpdates()
    fun requestSingleUpdate(request: OneshotRequest)
}