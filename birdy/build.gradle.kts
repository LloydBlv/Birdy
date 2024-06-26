plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
}

android {
    namespace = "com.flydroid.birdy"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    testOptions.unitTests.isIncludeAndroidResources = true

}

dependencies {
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("androidx.core:core:1.12.0")
    testImplementation(libs.androidx.rules)
    testImplementation(libs.kotlinx.coroutines.test.jvm)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockk)
    testImplementation(libs.assertk)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("birdy-android") {
                groupId = "com.github.flydroid"
                artifactId = "birdy-android"
                version = "1.0.0"

                afterEvaluate {
                    artifact(tasks.getByName("bundleReleaseAar"))
                }
            }
        }
    }
}