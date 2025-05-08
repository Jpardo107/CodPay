package com.jaime.codpay.data

data class PaquetesResponse(
    val status: String,
    val totalPaquetes: Int,
    val paquetes: List<Paquete>
)