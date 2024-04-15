package com.flydroid.birdy

import androidx.test.platform.app.InstrumentationRegistry
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.flydroid.birdy.domain.Tokens
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class AuthManagerTest {
    @Test
    fun `initially all tokens are null`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val authManager = AuthManagerDefault(
            context = context,
            httpClient = mockk()
        )

        assertThat(authManager.refreshToken).isNull()
        assertThat(authManager.accessToken).isNull()
        assertThat(authManager.accessTokenExpiry).isEqualTo(0L)
    }
    @Test
    fun `when authenticate doesnt return value tokens are still null`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val httpClient = mockk<HttpClient>(relaxed = true)
        val authManager = AuthManagerDefault(
            context = context,
            httpClient = httpClient
        )
        every { httpClient.authenticate() } returns null
        assertThat(authManager.refreshToken).isNull()
        assertThat(authManager.accessToken).isNull()
        assertThat(authManager.accessTokenExpiry).isEqualTo(0L)
    }
    @Test
    fun `when authenticate returns value tokens are set correctly`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val httpClient = mockk<HttpClient>(relaxed = true)
        val authManager = AuthManagerDefault(
            context = context,
            httpClient = httpClient
        )
        every { httpClient.authenticate() } returns Tokens("access", "refresh", 1000)
        authManager.authenticate()
        assertThat(authManager.refreshToken).isNotNull().isEqualTo("refresh")
        assertThat(authManager.accessToken).isNotNull().isEqualTo("access")
        assertThat(authManager.accessTokenExpiry).isEqualTo(1000L)
    }

    @Test
    fun `when refresh tokens are updated correctly`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val httpClient = mockk<HttpClient>(relaxed = true)
        val authManager = AuthManagerDefault(
            context = context,
            httpClient = httpClient
        )
        every { httpClient.authenticate() } returns Tokens("access", "refresh", 1000)
        authManager.authenticate()
        every { httpClient.refreshToken("refresh") } returns Tokens("access1", "refresh1", 2000)
        authManager.refreshToken()
        assertThat(authManager.refreshToken).isNotNull().isEqualTo("refresh")
        assertThat(authManager.accessToken).isNotNull().isEqualTo("access1")
        assertThat(authManager.accessTokenExpiry).isEqualTo(2000L)
    }
}