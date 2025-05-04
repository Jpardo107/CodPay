package com.jaime.codpay.data

data class Ruta(
    val idRuta: Int,
    val nombreRuta: String,
    val estadoRuta: String,
    val fechaRuta: String,
    val idConductor: Int,
    val nombreConductor: String,
    val idVehiculo: Int,
    val patenteVehiculo: String,
    val idEnvio: Int,
    val numeroRefPedidoB2C: String,
    val nombreClienteFinal: String,
    val direccionEntrega: String,
    val comunaEntrega: String,
    val regionEntrega: String
)

data class RutasResponse(
    val status: String,
    val data: List<Ruta>
)