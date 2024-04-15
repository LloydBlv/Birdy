package com.flydroid.birdy

import androidx.test.platform.app.InstrumentationRegistry
import com.flydroid.birdy.auth.AuthManagerDefault
import com.flydroid.birdy.domain.TokenExpiredException
import com.flydroid.birdy.domain.Tokens
import com.flydroid.birdy.http.HttpClient
import com.flydroid.birdy.sync.LocationSyncerDefault
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class LocationSyncerTest {
    @Test
    fun `when initially accessToken is null the HttpClient authenticate is called`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val httpClient = mockk<HttpClient>(relaxed = true)
        val authManager = AuthManagerDefault(
            context = context,
            httpClient = httpClient
        )
        val locationSyncerDefault = LocationSyncerDefault(
            appContext = context,
            debugLogger = TestDebugLogger(),
            authManager = authManager,
            httpClient = httpClient,
            apiKey = "apiKey"
        )
        locationSyncerDefault.syncLocation(1.0, 2.0)
        verify { httpClient.authenticate() }
    }
    @Test
    fun `when authenticate is called and succeeds, trackLocation() is called`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val httpClient = mockk<HttpClient>(relaxed = true)
        val authManager = AuthManagerDefault(
            context = context,
            httpClient = httpClient
        )
        val locationSyncerDefault = LocationSyncerDefault(
            appContext = context,
            debugLogger = TestDebugLogger(),
            authManager = authManager,
            httpClient = httpClient,
            apiKey = "apiKey"
        )
        every { httpClient.authenticate() } returns Tokens(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresAtMillis = 1000
        )
        locationSyncerDefault.syncLocation(1.0, 2.0)
        verify { httpClient.trackLocation(1.0, 2.0, "accessToken") }
    }

    @Test
    fun `when token expired, the refresh mechanism is called`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val httpClient = mockk<HttpClient>(relaxed = true)
        val authManager = AuthManagerDefault(
            context = context,
            httpClient = httpClient
        )
        val locationSyncerDefault = LocationSyncerDefault(
            appContext = context,
            debugLogger = TestDebugLogger(),
            authManager = authManager,
            httpClient = httpClient,
            apiKey = "apiKey"
        )
        every { httpClient.authenticate() } returns Tokens(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresAtMillis = 1000
        )
        every {
            httpClient.trackLocation(1.0, 2.0, "accessToken")
        } throws TokenExpiredException()
        locationSyncerDefault.syncLocation(1.0, 2.0)
        verify { httpClient.refreshToken("refreshToken") }
    }
    @Test
    fun `when token expired, the refresh mechanism is called and updates token correctly`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val httpClient = mockk<HttpClient>(relaxed = true)
        val authManager = AuthManagerDefault(
            context = context,
            httpClient = httpClient
        )
        val locationSyncerDefault = LocationSyncerDefault(
            appContext = context,
            debugLogger = TestDebugLogger(),
            authManager = authManager,
            httpClient = httpClient,
            apiKey = "apiKey"
        )
        every { httpClient.authenticate() } returns Tokens(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresAtMillis = 1000
        )
        every {
            httpClient.trackLocation(1.0, 2.0, "accessToken")
        } throws TokenExpiredException()
        locationSyncerDefault.syncLocation(1.0, 2.0)

        every {
            httpClient.refreshToken("refreshToken")
        } returns Tokens(
            accessToken = "newAccessToken",
            refreshToken = "refreshToken",
            expiresAtMillis = 2000
        )

        locationSyncerDefault.syncLocation(1.0, 2.0)
        verify { httpClient.trackLocation(1.0, 2.0, "newAccessToken") }
    }
}