package com.flydroid.birdy

import com.flydroid.birdy.domain.Tokens
import org.json.JSONObject

class HttpClientDefault(
    private val debugLogger: DebugLogger,
    private val apiKey: String,
) : HttpClient {
    private val networkHelper = NetworkHelper(debugLogger)
    override fun authenticate(): Tokens? {
        require(apiKey.isNotEmpty()) {
            "API key must be provided"
        }
        val url = "https://dummy-api-mobile.api.sandbox.bird.one/auth"
        val connection = networkHelper.createConnection(
            urlString = url,
            method = "POST",
            token = apiKey
        )
        return try {
            val response = networkHelper.sendJson(connection, JSONObject())
            val responseData = JSONObject(response)
            Tokens(
                accessToken = responseData.optString("accessToken"),
                refreshToken = responseData.optString("refreshToken"),
                expiresAtMillis = DateUtils.parseISODateToEpochMillis(responseData.optString("expiresAt"))
            )
        } catch (e: Exception) {
            debugLogger.log("Failed to authenticate: $e")
            null
        } finally {
            connection.disconnect()
        }
    }

    override fun refreshToken(refreshToken: String?): Tokens? {
        refreshToken ?: return null
        val url = "https://dummy-api-mobile.api.sandbox.bird.one/auth/refresh"
        val connection = networkHelper.createConnection(url, "POST", refreshToken)

        return try {
            val response = networkHelper.sendJson(connection, JSONObject())
            val responseData = JSONObject(response)
            Tokens(
                accessToken = responseData.getString("accessToken"),
                refreshToken = "refreshToken",
                expiresAtMillis = DateUtils.parseISODateToEpochMillis(responseData.getString("expiresAt"))
            )
        } catch (e: Exception) {
            debugLogger.logException(e)
            null
        } finally {
            connection.disconnect()
        }
    }

    override fun trackLocation(latitude: Double, longitude: Double, accessToken: String) {
        val url = "https://dummy-api-mobile.api.sandbox.bird.one/location"
        val connection = networkHelper.createConnection(
            url, "POST",
            accessToken
        )
        val json = JSONObject().apply {
            put("latitude", latitude)
            put("longitude", longitude)
        }

        try {
            networkHelper.sendJson(connection, json)
        } finally {
            connection.disconnect()
        }
    }
}