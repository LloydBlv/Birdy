package com.flydroid.birdy

import android.Manifest.permission
import android.location.Location
import androidx.test.platform.app.InstrumentationRegistry
import com.flydroid.birdy.auth.AuthManagerDefault
import com.flydroid.birdy.clients.BirdyGooglePlayServices
import com.flydroid.birdy.domain.LastLocationParams
import com.flydroid.birdy.domain.OneshotRequest
import com.flydroid.birdy.domain.toLastLocationRequest
import com.flydroid.birdy.http.HttpClient
import com.flydroid.birdy.location.LocationProviderDefault
import com.flydroid.birdy.sync.LocationSyncer
import com.flydroid.birdy.sync.LocationSyncerDefault
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.SocketTimeoutException


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class BirdyClientTest {

    @get:Rule
    var permissionRule = PermissionRule()

    private val testCoroutineDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when permission is not granted callback is not called`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.setMockMode(true)
        val locationSyncer = mockk<LocationSyncer>(relaxed = true)
        val debugLogger = TestDebugLogger()
        val locationProvider = LocationProviderDefault(
            appContext = context,
            debugLogger = debugLogger,
            fusedLocationClient = fusedLocationClient,

            )
        val birdyClient = BirdyGooglePlayServices(
            appContext = context,
            locationProvider = locationProvider,
            locationSyncer = locationSyncer,
            debugLogger = debugLogger,
            debugMode = true,
            apiKey = "test"
        )
        val mockLocation = Location("mock").apply {
            latitude = 37.7749
            longitude = -122.4194
        }
        fusedLocationClient.setMockLocation(mockLocation)

        birdyClient.requestSingleUpdate(
            OneshotRequest.LastKnownLocation(
                params = LastLocationParams()
            )
        )
        verify(inverse = true) { locationSyncer.syncLocation(any(), any()) }
    }

    @Test
    @GrantPermissions(value = [permission.ACCESS_FINE_LOCATION])
    fun `oneshot operation calls location syncer when requested`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val fusedLocationClient = mockk<FusedLocationProviderClient>()
        val locationSyncer = mockk<LocationSyncer>(relaxed = true)
        val debugLogger = TestDebugLogger()
        val locationProvider = LocationProviderDefault(
            appContext = context,
            debugLogger = debugLogger,
            fusedLocationClient = fusedLocationClient,
        )
        val birdyClient = BirdyGooglePlayServices(
            appContext = context,
            locationProvider = locationProvider,
            locationSyncer = locationSyncer,
            debugLogger = debugLogger,
            debugMode = true,
            apiKey = "test"
        )

        val request = OneshotRequest.LastKnownLocation(
            params = LastLocationParams()
        )
        val locationMock: Location = mockk()
        val locationTaskMock: Task<Location> = mockk(relaxed = true)
        val slot = slot<OnSuccessListener<Location>>()
        every { locationTaskMock.addOnSuccessListener(capture(slot)) } answers {
            slot.captured.onSuccess(locationTaskMock.result)
            locationTaskMock
        }
        every { locationTaskMock.result } returns locationMock
        every { fusedLocationClient.getLastLocation(request.params.toLastLocationRequest()) } returns locationTaskMock

        every { locationMock.latitude } returns 37.7749
        every { locationMock.longitude } returns -122.4194

        birdyClient.requestSingleUpdate(request)
        verify { locationSyncer.syncLocation(37.7749, -122.4194) }
    }
    @Test
    @GrantPermissions(value = [permission.ACCESS_FINE_LOCATION])
    fun `exceptions are caught in handler`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val fusedLocationClient = mockk<FusedLocationProviderClient>()
        val testDebugLogger = mockk<TestDebugLogger>(relaxed = true)
        val httpClient = mockk<HttpClient>(relaxed = true)
        val authManager = AuthManagerDefault(
            context = context,
            httpClient = httpClient
        )
        val locationSyncer = LocationSyncerDefault(
            appContext = context,
            debugLogger = testDebugLogger,
            authManager = authManager,
            httpClient = httpClient,
            apiKey = "test"
        )
        val locationProvider = LocationProviderDefault(
            appContext = context,
            debugLogger = testDebugLogger,
            fusedLocationClient = fusedLocationClient,
        )

        val birdyClient = BirdyGooglePlayServices(
            appContext = context,
            locationProvider = locationProvider,
            locationSyncer = locationSyncer,
            debugLogger = testDebugLogger,
            debugMode = true,
            ioDispatcher = testCoroutineDispatcher,
            apiKey = "test"
        )

        val request = OneshotRequest.LastKnownLocation(params = LastLocationParams())
        val locationMock: Location = mockk()
        val locationTaskMock: Task<Location> = mockk(relaxed = true)
        val slot = slot<OnSuccessListener<Location>>()
        every { locationTaskMock.addOnSuccessListener(capture(slot)) } answers {
            slot.captured.onSuccess(locationTaskMock.result)
            locationTaskMock
        }
        every { locationTaskMock.result } returns locationMock
        every { fusedLocationClient.getLastLocation(request.params.toLastLocationRequest()) } returns locationTaskMock

        every { locationMock.latitude } returns 37.7749
        every { locationMock.longitude } returns -122.4194

        val socketTimeoutException = SocketTimeoutException()
        every { httpClient.authenticate() } throws socketTimeoutException
        birdyClient.requestSingleUpdate(request)
        verify { testDebugLogger.logException(socketTimeoutException) }
    }
}