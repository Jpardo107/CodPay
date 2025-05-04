package com.jaime.codpay.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.jaime.codpay.data.Pedido
import com.jaime.codpay.ui.components.EntregarScreen.AditionalCommment
import com.jaime.codpay.ui.components.EntregarScreen.AdressDelivery
import com.jaime.codpay.ui.components.EntregarScreen.DeliveryHeaderInfo
import com.jaime.codpay.ui.components.EntregarScreen.PagoEfectivoDialog
import com.jaime.codpay.ui.components.EntregarScreen.PagoTarjetaDialog
import com.jaime.codpay.ui.components.EntregarScreen.PaymentDialog
import com.jaime.codpay.ui.components.EntregarScreen.Recaudation
import com.jaime.codpay.ui.components.EntregarScreen.RecipientCard
import com.jaime.codpay.ui.components.InitRoute.TitleSection
import com.jaime.codpay.utils.AbrirNavegacion
import com.jaime.codpay.utils.LlamarTelefono

@Composable
fun EntregarScreen(navController: NavController, pedidoJson: String) {
    var mostrarDialogoPayment by remember { mutableStateOf(false) }
    var mostrarDialogoEfectivo by remember { mutableStateOf(false) }
    var pagoRecibido by remember { mutableStateOf("") }
    var mostrarDialogoTarjeta by remember { mutableStateOf(false) }

    // Deserializar el JSON a un objeto Pedido
    val gson = Gson()
    val pedidoData: Pedido? = try {
        gson.fromJson(pedidoJson, Pedido::class.java)
    } catch (e: Exception) {
        Log.e("EntregarScreen", "Error al deserializar el JSON", e)
        null
    }

    if (mostrarDialogoTarjeta) {
        PagoTarjetaDialog(
            onPagoExitoso = {
                mostrarDialogoTarjeta = false
                navController.navigate("delivery_package_screen")
            },
            onPagoFallido = {
                mostrarDialogoTarjeta = false
                navController.popBackStack() // Vuelve al DeliveryScreen
            },
            onDismiss = {
                mostrarDialogoTarjeta = false
            }
        )
    }

    if (mostrarDialogoPayment) {
        PaymentDialog(
            onDismiss = { mostrarDialogoPayment = false },
            onPagoTarjeta = {
                mostrarDialogoTarjeta = true
                mostrarDialogoPayment = false
            },
            onPagoEfectivo = {
                mostrarDialogoEfectivo = true
                mostrarDialogoPayment = false
            }
        )
    }

    if (mostrarDialogoEfectivo) {
        PagoEfectivoDialog(
            valorRecaudar = pedidoData?.valorRecaudar?.toDouble() ?: 0.0,
            pagoRecibido = pagoRecibido,
            onPagoRecibidoChange = { pagoRecibido = it },
            onPagoExitoso = {
                mostrarDialogoEfectivo = false
                navController.navigate("delivery_package_screen")
            },
            onPagoErroneo = {
                mostrarDialogoEfectivo = false
                mostrarDialogoPayment = true
            },
            onDismiss = { mostrarDialogoEfectivo = false }
        )
    }

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(28.dp))
        TitleSection(nombre = "Pedido ${pedidoData?.numeroPedidoCodpay ?: "N/A"}")
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            DeliveryHeaderInfo(
                fecha = pedidoData?.fechaPedido?.substring(0, 10) ?: "N/A",
                horaIncio = pedidoData?.fechaPedido?.substring(11, 16) ?: "N/A",
                horaFin = "N/A" // No tenemos hora fin en el JSON
            )
            RecipientCard(
                nombreDestinatario = pedidoData?.clienteFinal?.nombreClienteFinal ?: "N/A",
                onLlamarClick = {
                    pedidoData?.clienteFinal?.telefonoClienteFinal?.let {
                        LlamarTelefono(context, it)
                    }
                }
            )
            AdressDelivery(
                direccionCliente = pedidoData?.clienteFinal?.direccionEntrega ?: "N/A",
                regionCliente = pedidoData?.clienteFinal?.regionEntrega ?: "N/A",
                comunaCliente = pedidoData?.clienteFinal?.comunaEntrega ?: "N/A",
                onMaps = {
                    val direc =
                        "${pedidoData?.clienteFinal?.direccionEntrega ?: ""}, ${pedidoData?.clienteFinal?.comunaEntrega ?: ""}"
                    AbrirNavegacion(context, direc)
                }
            )
            AditionalCommment(comentario = pedidoData?.clienteFinal?.referenciaDireccion ?: "N/A")
            Recaudation(
                amount = pedidoData?.valorRecaudar?.toDouble() ?: 0.0,
                onRecaudarClick = {
                    mostrarDialogoPayment = true
                }
            )
        }

    }
}