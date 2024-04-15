package com.flydroid.birdy.domain

data class Tokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresAtMillis: Long
)