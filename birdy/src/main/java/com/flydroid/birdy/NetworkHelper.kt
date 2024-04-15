package com.flydroid.birdy

import android.util.Log
import com.flydroid.birdy.domain.TokenExpiredException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class NetworkHelper(
    private val debugLogger: DebugLogger
) {
    /**
     * Sets up and returns an HttpURLConnection for the given URL and method.
     * Adds headers for 'Content-Type' as JSON and Authorization as Bearer token if provided.
     *
     * @param urlString The full URL as a string to connect to.
     * @param method The HTTP method (POST, GET, etc.).
     * @param token Optional Bearer token for authorization.
     * @return A configured HttpURLConnection.
     */
    fun createConnection(urlString: String, method: String, token: String? = null): HttpURLConnection {
        debugLogger.log("url=$urlString, method=$method, token=$token")
        val url = URL(urlString)
        return (url.openConnection() as HttpURLConnection).apply {
            requestMethod = method
            setRequestProperty("Content-Type", "application/json")
            if (token != null) {
                setRequestProperty("Authorization", "Bearer $token")
            }
            if (method == "POST") {
                doOutput = true  // Allow sending data for POST methods
            }
        }
    }

    /**
     * Sends the given JSON object to the server using the provided HttpURLConnection.
     *
     * @param connection The pre-configured HttpURLConnection to use.
     * @param json The JSON object to send in the request body.
     * @return The server's response as a String.
     */
    fun sendJson(connection: HttpURLConnection, json: JSONObject? = null): String {
        debugLogger.log("POST $json to ${connection.url}", level = Log.INFO)

        connection.outputStream.use { os ->
            os.write(json.toString().toByteArray(Charsets.UTF_8))
        }
        connection.connect()

        val text = connection.inputStream.bufferedReader().use { it.readText() }
        debugLogger.log("response(${connection.responseCode}): $text", level = Log.WARN)
        when (connection.responseCode) {
            HttpURLConnection.HTTP_OK -> {
                return text
            }
            HttpURLConnection.HTTP_FORBIDDEN -> {
                throw TokenExpiredException()
            }
            else -> {
                throw RuntimeException("HTTP error code: ${connection.responseCode}")
            }
        }
    }}