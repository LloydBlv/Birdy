package com.flydroid.birdy.sync

import android.content.Context
import android.util.Log
import com.flydroid.birdy.debug.DebugLogger
import com.flydroid.birdy.auth.AuthManager
import com.flydroid.birdy.auth.AuthManagerDefault
import com.flydroid.birdy.domain.TokenExpiredException
import com.flydroid.birdy.http.HttpClient
import com.flydroid.birdy.http.HttpClientDefault

class LocationSyncerDefault(
    private val appContext: Context,
    private val debugLogger: DebugLogger,
    apiKey: String,
    private val httpClient: HttpClient = HttpClientDefault(debugLogger, apiKey),
    private val authManager: AuthManager = AuthManagerDefault(appContext, httpClient),
) : LocationSyncer {

    override fun syncLocation(latitude: Double, longitude: Double) {
        if (authManager.accessToken == null) {
            val success = authManager.authenticate()
            debugLogger.log("Authenticated: $success")
            if (!success) return
        } else if (isTokenExpired()) {
            val success = authManager.refreshToken()
            debugLogger.log("Token refreshed: $success")
        }

        try {
            httpClient.trackLocation(
                latitude = latitude,
                longitude = longitude,
                token = authManager.accessToken!!
            )
        } catch (e: Exception) {
            debugLogger.logException(e)
            if (e is TokenExpiredException) {
                authManager.refreshToken()
            }
        }

    }


    private fun isTokenExpired(): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val tokenExpiryTime = authManager.accessTokenExpiry!!
        val timeUntilExpiration =
            (tokenExpiryTime - currentTimeMillis) / 1000  // Convert milliseconds to seconds

        debugLogger.log(
            "Token expires in: $timeUntilExpiration seconds",
            level = Log.ERROR
        )

        return currentTimeMillis > tokenExpiryTime
    }

}