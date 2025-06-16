package com.jaime.codpay.data

fun Envio.toPedido(): Pedido {
    return Pedido(
        idPedido = this.idPedido,
        cantidadPaquetes = this.cantidadPaquetes,
        paquetes = this.paquetes,
        estadoPedido = this.estadoPedido,
        clienteFinal = this.clienteFinal
    )
}
