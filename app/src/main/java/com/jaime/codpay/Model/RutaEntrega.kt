package com.jaime.codpay.Model

data class RutaEntrega(
    val id: Int,
    val cliente: String,
    val direccion: String,
    val comuna: String,
    val pedido: Int
)
