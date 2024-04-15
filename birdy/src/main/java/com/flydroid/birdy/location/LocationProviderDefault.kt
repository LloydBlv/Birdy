package com.flydroid.birdy.location


import android.content.Context
import android.location.Location
import com.flydroid.birdy.debug.DebugLogger
import com.flydroid.birdy.domain.ObserveParams
import com.flydroid.birdy.domain.OneshotRequest
import com.flydroid.birdy.domain.toCurrentLocationRequest
import com.flydroid.birdy.domain.toLastLocationRequest
import com.flydroid.birdy.domain.toLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices

class LocationProviderDefault(
    appContext: Context,
    private val debugLogger: DebugLogger,
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)
) : LocationProvider {

    override val isObserving: Boolean
        get() = locationListener != null

    private var locationListener: LocationListener? = null
    private var locationReceivedListener: LocationProvider.OnLocationReceivedListener? = null

    override fun requestUpdates(params: ObserveParams) {
        locationListener = LocationListener {
            debugLogger.log("received location = $it")
            locationReceivedListener?.onLocationReceived(it.latitude, it.longitude)
        }
        fusedLocationClient.requestLocationUpdates(
            params.toLocationRequest(),
            locationListener!!,
            null /* Looper */
        )
    }

    override fun removeUpdates() {
        locationListener?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        locationListener = null
    }

    override fun requestLastLocation(request: OneshotRequest.LastKnownLocation) {
        debugLogger.log("requestLastLocation, request=$request")
        fusedLocationClient.getLastLocation(request.params.toLastLocationRequest())
            .addOnSuccessListener { location: Location? ->
                debugLogger.log("getLastLocation.addOnSuccessListener=$location")
                location ?: return@addOnSuccessListener
                locationReceivedListener?.onLocationReceived(location.latitude, location.longitude)
            }.addOnFailureListener {
                debugLogger.logException(it)
            }.addOnCanceledListener {
                debugLogger.log("getLastLocation.addOnCanceledListener")
            }
    }

    override fun requestCurrentLocation(request: OneshotRequest.FreshLocation) {
        fusedLocationClient.getCurrentLocation(
            request.params.toCurrentLocationRequest(),
            null
        ).addOnSuccessListener { location: Location? ->
            debugLogger.log("OneshotRequest.FreshLocation.addOnSuccessListener=$location")
            location?.let {
                locationReceivedListener?.onLocationReceived(it.latitude, it.longitude)
            }
        }.addOnFailureListener {
            debugLogger.logException(it)
        }
    }

    override fun setLocationReceivedListener(listener: LocationProvider.OnLocationReceivedListener) {
        locationReceivedListener = listener
    }


}