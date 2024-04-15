package com.flydroid.birdy.http

import com.flydroid.birdy.domain.Tokens

interface HttpClient {
    fun authenticate(): Tokens?
    fun refreshToken(refreshToken: String?): Tokens?
    fun trackLocation(
        latitude: Double,
        longitude: Double,
        token: String
    )
}