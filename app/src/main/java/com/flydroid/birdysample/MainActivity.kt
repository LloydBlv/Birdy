package com.flydroid.birdysample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import com.flydroid.birdy.Birdy
import com.flydroid.birdy.domain.LastLocationParams
import com.flydroid.birdy.domain.ObserveParams
import com.flydroid.birdy.domain.OneshotRequest
import com.flydroid.birdy.hasCoarseLocationPermission
import com.flydroid.birdy.hasFineLocationPermission
import com.flydroid.birdysample.ui.theme.BirdySampleTheme

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    private fun requestPermissions() {
        if (hasFineLocationPermission() && hasCoarseLocationPermission()) {
            Toast.makeText(this, "Location permissions are already granted!", Toast.LENGTH_SHORT)
                .show()
            return
        }
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), 0
        )
    }

    override fun onStop() {
        super.onStop()
        Birdy.stopLocationUpdates()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Birdy.init(
            context = this,
            debugMode = true,
            apiKey = "xdk8ih3kvw2c66isndihzke5",
            debugLogger = mainViewModel
        )
        setContent {
            BirdySampleTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopAppBar(title = { Text("Birdy Sample App") }) },
                    content = { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ButtonsView()
                            val logs by mainViewModel.state.collectAsState(initial = emptyList())
                            val state = rememberLazyListState()
                            LazyColumn(
                                state = state,
                                modifier = Modifier.fillMaxSize(),
                                reverseLayout = true
                            ) {
                                items(logs) {
                                    when (it) {
                                        is Log.Message -> MessageItem(
                                            modifier = Modifier.animateItemPlacement(),
                                            message = it.message,
                                            level = it.level
                                        )

                                        is Log.Exception -> ExceptionItem(it)
                                    }
                                    HorizontalDivider()
                                }
                            }
                        }
                    })
            }
        }
    }

    @Composable
    private fun ExceptionItem(it: Log.Exception) {
        ListItem(
            modifier = Modifier,
            overlineContent = {
                Text("Exception", color = Color.Red)
            },
            headlineContent = {
                Text(
                    "Exception: ${it.exception.message}",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            })
    }

    @Composable
    private fun MessageItem(modifier: Modifier, message: String, level: Int) {
        ListItem(
            modifier = modifier.clickable { },
            headlineContent = {
                val color = when (level) {
                    android.util.Log.DEBUG -> Color.Gray
                    android.util.Log.INFO -> Color.Blue
                    android.util.Log.WARN -> Color.Green
                    android.util.Log.ERROR -> Color.Red
                    else -> Color.Gray
                }
                Text(
                    message, color = color,
                    style = MaterialTheme.typography.labelLarge
                )
            })
    }

    @Composable
    private fun ButtonsView() {
        Row {
            Button(onClick = { Birdy.startLocationUpdates(ObserveParams()) }) {
                Text("Start Location Updates")
            }
            Button(onClick = { Birdy.stopLocationUpdates() }) {
                Text("Stop Location Updates")
            }
        }

        Row {
            Button(onClick = {
                Birdy.requestSingleUpdate(
                    OneshotRequest.LastKnownLocation(
                        LastLocationParams()
                    )
                )
            }) {
                Text("Oneshot location (last known)")
            }
            Button(onClick = {
                Birdy.requestSingleUpdate(
                    OneshotRequest.FreshLocation(
                        LastLocationParams()
                    )
                )
            }) {
                Text("Oneshot location (fresh)")
            }
        }

        Row {
            OutlinedButton(onClick = { mainViewModel.clearLogs() }) {
                Text("Clear logs")
            }
            OutlinedButton(onClick = ::requestPermissions) {
                Text("Request permission")
            }
        }
    }
}
