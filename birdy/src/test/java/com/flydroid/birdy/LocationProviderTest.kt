package com.flydroid.birdy

import androidx.test.platform.app.InstrumentationRegistry
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.flydroid.birdy.domain.ObserveParams
import com.google.android.gms.location.LocationServices
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class LocationProviderTest {
    @Test
    fun `location provider sets isObserving flag correctly when updates are requested`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val locationProvider = LocationProviderDefault(
            appContext = context,
            debugLogger = TestDebugLogger(),
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        )
        locationProvider.requestUpdates(ObserveParams())
        assertk.assertThat(locationProvider.isObserving).isTrue()
        locationProvider.removeUpdates()
        assertk.assertThat(locationProvider.isObserving).isFalse()
    }
}