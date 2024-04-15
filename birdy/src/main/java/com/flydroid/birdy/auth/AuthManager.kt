package com.flydroid.birdy.auth

interface AuthManager {
    var accessToken: String?
    var refreshToken: String?
    var accessTokenExpiry: Long?

    fun authenticate(): Boolean
    fun refreshToken(): Boolean
}