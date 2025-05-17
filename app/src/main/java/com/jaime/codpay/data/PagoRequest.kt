package com.jaime.codpay.data

import com.google.gson.annotations.SerializedName

data class PagoRequest(
    @SerializedName("idEnvio")
    val idEnvio: Int,

    @SerializedName("idEmpresaB2B")
    val idEmpresaB2B: Int,

    @SerializedName("metodoPago")
    val metodoPago: String,

    @SerializedName("referenciaTransaccion")
    val referenciaTransaccion: String?, // Puede ser nulo

    @SerializedName("observacionesPago")
    val observacionesPago: String,

    @SerializedName("montoPagado")
    val montoPagado: Int // O Double/Float si puede tener decimales, pero tu ejemplo usa Int
)