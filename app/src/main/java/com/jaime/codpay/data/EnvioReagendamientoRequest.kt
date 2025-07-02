package com.jaime.codpay.data

data class EnvioReagendamientoRequest(
    val idEnvio: Int,
    val estadoEnvio: String,
    val fechaReprogramada: String
)
