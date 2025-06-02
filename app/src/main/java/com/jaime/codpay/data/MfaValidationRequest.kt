package com.jaime.codpay.data

data class MfaValidationRequest(
    val email: String,
    val codigo: String
)
