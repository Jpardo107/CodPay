package com.jaime.codpay.data

import com.google.gson.annotations.SerializedName

class PagoResponse (
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("idPagoPedido")
    val idPagoPedido: Int
)