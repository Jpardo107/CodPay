package com.jaime.codpay.data

import com.google.gson.annotations.SerializedName

data class EnvioResponse(
    val status: String,
    val data: List<Envio>
)

data class Envio(
    val idEnvio: Int,
    val idPedido: Int,
    val estadoEnvio: String,
    val fechaEnvio: String,
    val fechaEnvioReprogramado: String,
    val idRuta: Int,
    val numeroRefPedidoB2C: String,
    @SerializedName("idClienteB2C")
    val idClienteB2C: Int, // ← AÑADIR ESTE CAMPO
    @SerializedName("nombreClienteB2C")
    val nombreClienteB2C: String = "Default",
    val estadoPedido: String,
    val cantidadPaquetes: Int,
    val clienteFinal: ClienteFinal,
    val valorRecaudar: Double,
    val paquetes: List<Paquete>
)

data class ClienteFinal(
    val nombreClienteFinal: String,
    val rutClienteFinal: String,
    val emailClienteFinal: String,
    val telefonoClienteFinal: String,
    val direccionEntrega: String,
    val referenciaDireccion: String,
    val regionEntrega: String,
    val comunaEntrega: String
)

data class PaqueteEnvio(
    val idPaquete: Int,
    val codigoPaquete: String,
    val descripcionPaquete: String,
    val codigoQr: String
)

data class PaqueteConEnvio(
    val idPaquete: Int,
    val codigoPaquete: String,
    val descripcionPaquete: String,
    val codigoQr: String,
    val idEnvio: Int,
    val numeroRefPedidoB2C: String,
    @SerializedName("idClienteB2C")
    val idClienteB2C: Int,
    @SerializedName("nombreClienteB2C")
    val nombreClienteB2C: String,
    val clienteNombre: String,
    val direccionEntrega: String,
    val comunaEntrega: String
)
