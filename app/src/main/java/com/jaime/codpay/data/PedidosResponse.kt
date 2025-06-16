package com.jaime.codpay.data

data class PedidosResponse(
    val status: String,
    val data: List<Envio> // puedes usar Envio directamente
)
