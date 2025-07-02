// data/ActualizarRutaRequest.kt
package com.jaime.codpay.data

data class ActualizarRutaRequest(
    val idRuta: Int,
    val agregar: List<Int>,
    val quitar: List<Int>,
    val estadoRuta: String
)

data class ActualizarEstadoRutaRequest(
    val idRuta: Int,
    val estadoRuta: String
)
