package com.jaime.codpay.data

import com.jaime.codpay.data.PagoRequest
import com.jaime.codpay.data.PagoResponse
import retrofit2.Response

class PagosRepository(private val apiService: ApiService) {
    suspend fun registrarPagoEnServidor(pagoDetails: PagoRequest): Response<PagoResponse> {
        return apiService.registrarPago(pagoDetails)
    }
}