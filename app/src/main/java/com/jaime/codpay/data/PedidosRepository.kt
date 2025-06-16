package com.jaime.codpay.data

interface PedidosRepository {
    suspend fun getPedidos(idEmpresa: Int): List<Pedido>

    suspend fun actualizarRuta(
        idRuta: Int,
        agregar: List<Int>,
        quitar: List<Int>,
        estadoRuta: String
    ): Boolean
}
