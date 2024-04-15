package com.flydroid.birdy

import com.flydroid.birdy.domain.ObserveParams
import com.flydroid.birdy.domain.OneshotRequest

interface BirdyClient {
    fun startLocationUpdates(request: ObserveParams)
    fun stopLocationUpdates()
    fun requestSingleUpdate(request: OneshotRequest)
}