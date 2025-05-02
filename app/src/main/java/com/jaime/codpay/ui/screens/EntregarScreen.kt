package com.jaime.codpay.ui.screens

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
fun EntregarScreen(navController: NavController) {
    var mostrarDialogoPayment by remember { mutableStateOf(false) }
    var mostrarDialogoEfectivo by remember { mutableStateOf(false) }
    var pagoRecibido by remember { mutableStateOf("") }
    var valorRecaudar = 12000.0 // Este vendrá luego de la API
    var mostrarDialogoTarjeta by remember { mutableStateOf(false) }

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
            valorRecaudar = valorRecaudar,
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
        TitleSection(nombre = "Pedido PED123456")
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            DeliveryHeaderInfo(fecha = "01/01/2025", "15:00", horaFin = "15:05")
            RecipientCard(
                nombreDestinatario = "Juanito Perez Echeverria",
                onLlamarClick = { LlamarTelefono(context, "+56928461185") }
            )
            AdressDelivery(
                direccionCliente = "Dos Oriente 429",
                regionCliente = "Los Rios",
                comunaCliente = "Valdivia",
                onMaps = {
                    val direc = "Dos Oriente 429, Valdivia"
                    AbrirNavegacion(context, direc)
                }
            )
            AditionalCommment(comentario = "Casa con araucaria afuera")
            Recaudation(
                amount = valorRecaudar,
                onRecaudarClick = {
                    mostrarDialogoPayment = true
                }
            )
        }

    }
}