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
import com.jaime.codpay.data.Envio
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
import kotlin.text.substring
import kotlin.text.toDouble

@Composable
fun EntregarScreen(navController: NavController, envioJson: String) {
    var mostrarDialogoPayment by remember { mutableStateOf(false) }
    var mostrarDialogoEfectivo by remember { mutableStateOf(false) }
    var pagoRecibido by remember { mutableStateOf("") }
    var mostrarDialogoTarjeta by remember { mutableStateOf(false) }

    // Deserializar el JSON a un objeto Envio
    val gson = Gson()
    val envioData: Envio? = try {
        gson.fromJson(envioJson, Envio::class.java)
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
            //valorRecaudar = envioData?.valorRecaudar?.toDouble() ?: 0.0,
            valorRecaudar = 15000.0,
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
        TitleSection(nombre = "Envio: ${envioData?.numeroRefPedidoB2C ?: "N/A"}")
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            DeliveryHeaderInfo(
                fecha = envioData?.fechaEnvio?.substring(0, 10) ?: "N/A",
                horaIncio = envioData?.fechaEnvio?.substring(11, 16) ?: "N/A",
                horaFin = "N/A" // No tenemos hora fin en el JSON
            )
            RecipientCard(
                nombreDestinatario = envioData?.clienteFinal?.nombreClienteFinal ?: "N/A",
                onLlamarClick = {
                    envioData?.clienteFinal?.telefonoClienteFinal?.let {
                        LlamarTelefono(context, it)
                    }
                }
            )
            AdressDelivery(
                direccionCliente = envioData?.clienteFinal?.direccionEntrega ?: "N/A",
                regionCliente = envioData?.clienteFinal?.regionEntrega ?: "N/A",
                comunaCliente = envioData?.clienteFinal?.comunaEntrega ?: "N/A",
                onMaps = {
                    val direc =
                        "${envioData?.clienteFinal?.direccionEntrega ?: ""}, ${envioData?.clienteFinal?.comunaEntrega ?: ""}"
                    AbrirNavegacion(context, direc)
                }
            )
            AditionalCommment(comentario = envioData?.clienteFinal?.referenciaDireccion ?: "N/A")
            Recaudation(
                //amount = envioData?.valorRecaudar?.toDouble() ?: 0.0,
                amount = 15000.0,
                onRecaudarClick = {
                    mostrarDialogoPayment = true
                }
            )
        }

    }
}