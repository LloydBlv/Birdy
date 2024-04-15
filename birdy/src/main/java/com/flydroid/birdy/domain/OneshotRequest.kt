package com.flydroid.birdy.domain

sealed interface OneshotRequest {
    val params: LastLocationParams

    data class LastKnownLocation(override val params: LastLocationParams) : OneshotRequest
    data class FreshLocation(override val params: LastLocationParams) : OneshotRequest
}