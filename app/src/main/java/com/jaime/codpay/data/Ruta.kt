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
    val idEnvio: List<Int>
    //val idEnvio: Int
)