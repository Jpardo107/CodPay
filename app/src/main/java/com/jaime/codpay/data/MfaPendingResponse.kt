package com.jaime.codpay.data

data class MfaPendingResponse(
    val status: String,
    val mfa: String,
    val message: String
)