package com.flydroid.birdy

interface AuthManager {
    var accessToken: String?
    var refreshToken: String?
    var accessTokenExpiry: Long?

    fun authenticate(): Boolean
    fun refreshToken(): Boolean
}