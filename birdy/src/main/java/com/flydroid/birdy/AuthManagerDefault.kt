package com.flydroid.birdy

import android.content.Context
import android.content.SharedPreferences

class AuthManagerDefault(
    context: Context,
    private val httpClient: HttpClient
) : AuthManager {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override var accessToken: String?
        get() = preferences.getString("ACCESS_TOKEN", null)
        set(value) = preferences.edit().putString("ACCESS_TOKEN", value).apply()

    override var refreshToken: String?
        get() = preferences.getString("REFRESH_TOKEN", null)
        set(value) = preferences.edit().putString("REFRESH_TOKEN", value).apply()

    override var accessTokenExpiry: Long?
        get() = preferences.getLong("ACCESS_TOKEN_EXPIRY", 0L)
        set(value) = preferences.edit().putLong("ACCESS_TOKEN_EXPIRY", value ?: 0L).apply()

    override fun authenticate(): Boolean {
        val (token, refresh, expiry) = httpClient.authenticate() ?: return false
        this.accessToken = token
        this.refreshToken = refresh
        this.accessTokenExpiry = expiry
        return true
    }

    override fun refreshToken(): Boolean {
        val (token, _, expiry) = httpClient.refreshToken(refreshToken) ?: return false
        this.accessToken = token
        this.accessTokenExpiry = expiry
        return true
    }
}