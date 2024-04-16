[![](https://jitpack.io/v/LloydBlv/Birdy.svg)](https://jitpack.io/#LloydBlv/Birdy)

# Birdy SDK

The Birdy SDK is a comprehensive Android library designed to facilitate real-time and on-demand location tracking. Utilizing Android's Fused Location Provider, Birdy offers precise location updates efficiently, suitable for applications that depend on reliable location data.

## Features

- **Real-Time Location Updates**: Automatically capture and transmit location updates in real-time.
- **On-Demand Location Access**: Fetch the current location as needed with minimal setup.
- **Effortless Integration**: Integrate Birdy effortlessly into any Android project.
- **Battery Optimization**: Leverages battery-efficient APIs to extend device usage times without sacrificing accuracy.
- **Customizable Settings**: Adjustable settings to tailor location update intervals and accuracy to your needs.

## Getting Started

### Prerequisites

- Android SDK 21 (Lollipop) or higher.
- Android Studio 2023.3.1 Jellyfish | RC 2 or higher recommended.
- Gradle 8.3.2 for building the project.

### Installation

#### Step 1: Add the JitPack repository

Add this line to your root `build.gradle` at the end of repositories:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

#### Step 2: Add the Birdy SDK dependency

Add the following line to your app's `build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.LloydBlv:Birdy:latest.release.here'
}
```
Replace `latest.release.here` with the latest release version of Birdy.

### Usage

#### Initializing Birdy

```kotlin
import com.yourdomain.birdy.Birdy

// Initialize Birdy in your Application or Activity
Birdy.init(
    context = this,
    debugMode = true,
    apiKey = "YOUR_API_KEY_HERE"
)
```

#### Starting and Stopping Location Updates

```kotlin
// To start location updates with specific parameters
val params = ObserveParams(
    priority = Priority.BALANCED,
    updateInterval = 60.seconds,
    minUpdateInterval = 30.seconds,
    stopAfter = 1.hours
)
Birdy.startLocationUpdates(params)

// To stop location updates
Birdy.stopLocationUpdates()
```

#### Requesting a Single Location Update

```kotlin
// To request the last known location
Birdy.requestSingleUpdate(OneshotRequest.LastKnownLocation(LastLocationParams()))

// To request a fresh location
Birdy.requestSingleUpdate(OneshotRequest.FreshLocation(LastLocationParams()))
```

### Data Classes and Interfaces

#### ObserveParams

Used to specify the conditions under which location updates should be observed.

- **`priority`**: The priority of the location request (e.g., high accuracy, balanced).
- **`updateInterval`**: How often location updates should occur.
- **`minUpdateInterval`**: The minimum interval between location updates to ensure battery efficiency.
- **`stopAfter`**: Automatically stop updates after a certain duration.

#### OneshotRequest

A sealed interface for handling single location requests. It has two implementations:

- **`LastKnownLocation`**: Requests the last available location.
- **`FreshLocation`**: Requests a new location fix.
