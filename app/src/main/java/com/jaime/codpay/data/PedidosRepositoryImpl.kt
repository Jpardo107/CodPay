package com.jaime.codpay.data

import android.util.Log

class PedidosRepositoryImpl : PedidosRepository {

    private val apiService = RetrofitClient.instance

    override suspend fun getPedidos(idEmpresa: Int): List<Pedido> {
        val response = apiService.getPedidos(idEmpresa)
        if (response.isSuccessful) {
            val pedidosResponse = response.body()
            Log.d("PedidosRepositoryImpl", "Pedidos response: $pedidosResponse")
            return pedidosResponse?.data?.map { it.toPedido() } ?: emptyList()
        } else {
            throw Exception("Error en la API Pedidos: ${response.code()}")
        }
    }

    override suspend fun actualizarRuta(
        idRuta: Int,
        agregar: List<Int>,
        quitar: List<Int>,
        estadoRuta: String
    ): Boolean {
        val body = ActualizarRutaRequest(
            idRuta = idRuta,
            agregar = agregar,
            quitar = quitar,
            estadoRuta = estadoRuta
        )

        val response = apiService.actualizarRuta(body)
        return response.isSuccessful
    }


}
