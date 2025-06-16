package com.jaime.codpay.data

data class Pedido(
    val idPedido: Int,
    val cantidadPaquetes: Int,
    val paquetes: List<Paquete>,
    val estadoPedido: String,
    val clienteFinal: ClienteFinal
)
