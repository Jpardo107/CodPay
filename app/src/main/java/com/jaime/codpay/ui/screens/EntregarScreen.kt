package com.jaime.codpay.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.jaime.codpay.data.Envio
import com.jaime.codpay.data.EnviosRepositoryImpl
import com.jaime.codpay.data.PagosRepository
import com.jaime.codpay.data.RetrofitClient
import com.jaime.codpay.data.UserDataStore
import com.jaime.codpay.ui.components.EntregarScreen.AditionalCommment
import com.jaime.codpay.ui.components.EntregarScreen.AdressDelivery
import com.jaime.codpay.ui.components.EntregarScreen.DeliveryHeaderInfo
import com.jaime.codpay.ui.components.EntregarScreen.PagoEfectivoDialog
import com.jaime.codpay.ui.components.EntregarScreen.PagoTarjetaDialog
import com.jaime.codpay.ui.components.EntregarScreen.PaymentDialog
import com.jaime.codpay.ui.components.EntregarScreen.Recaudation
import com.jaime.codpay.ui.components.EntregarScreen.RecipientCard
import com.jaime.codpay.ui.components.InitRoute.TitleSection
import com.jaime.codpay.ui.viewmodel.EntregarViewModel
import com.jaime.codpay.ui.viewmodel.EntregarViewModelFactory
import com.jaime.codpay.ui.viewmodel.ResultadoPago
import com.jaime.codpay.utils.AbrirNavegacion
import com.jaime.codpay.utils.LlamarTelefono
import kotlin.text.substring
import kotlin.text.toDouble

@Composable
fun EntregarScreen(navController: NavController, envioJson: String) {
    val context = LocalContext.current
    var mostrarDialogoPayment by remember { mutableStateOf(false) }
    var mostrarDialogoEfectivo by remember { mutableStateOf(false) }
    var pagoRecibido by remember { mutableStateOf("") }
    var mostrarDialogoTarjeta by remember { mutableStateOf(false) }


    val apiService = remember { RetrofitClient.instance }
    val pagosRepository = remember { PagosRepository(apiService) }
    val userDataStore = remember { UserDataStore(context) }
    val enviosRepository = remember { EnviosRepositoryImpl() }

    val entregarViewModelFactory = remember(pagosRepository) {
        EntregarViewModelFactory(pagosRepository, enviosRepository)
    }
    val viewModel: EntregarViewModel = viewModel(factory = entregarViewModelFactory)

    val idEmpresaB2BFromStore by userDataStore.getUserIdEmpresa.collectAsState(initial = null)


    // Deserializar el JSON a un objeto Envio
    val gson = Gson()
    val envioData: Envio? = remember(envioJson) {
        try {
            gson.fromJson(envioJson, Envio::class.java)
        } catch (e: Exception) {
            Log.e("EntregarScreen", "Error al deserializar el JSON", e)
            null
        }
    }
    val envioEntregado = envioData?.estadoEnvio == "Entregado"

    // Pasar datos al ViewModel cuando estén disponibles
    LaunchedEffect(envioData, idEmpresaB2BFromStore) {
        if (envioData != null && idEmpresaB2BFromStore != null) {
            viewModel.envioActual = envioData
            viewModel.idEmpresaB2B = idEmpresaB2BFromStore
            Log.d(
                "EntregarScreen",
                "EnvioData ID: ${envioData.idEnvio}, idEmpresaB2B: $idEmpresaB2BFromStore seteados en ViewModel"
            )
        } else {
            Log.d(
                "EntregarScreen",
                "Esperando envioData (${envioData != null}) o idEmpresaB2B (${idEmpresaB2BFromStore != null})"
            )
        }
    }

    val resultadoPagoState by viewModel.resultadoPago.collectAsState()

    // Observar el resultado del pago para manejar la UI y la navegación
    LaunchedEffect(resultadoPagoState) {
        when (val resultado = resultadoPagoState) {
            is ResultadoPago.Success -> {
                Log.i("EntregarScreen", "Pago exitoso: ${resultado.pagoResponse.message}")
                Toast.makeText(
                    context,
                    "Pago exitoso: ${resultado.pagoResponse.message}",
                    Toast.LENGTH_LONG
                ).show()
                mostrarDialogoEfectivo = false

                // 🔁 Actualizar estado del envío a "Entregado"
                val envio = viewModel.envioActual
                if (envio != null) {
                    viewModel.actualizarEstadoEnvio(envio.idEnvio, "Entregado") { exito ->
                        if (exito) {
                            Log.i("EntregarScreen", "Estado del envío actualizado a 'Entregado'")
                            navController.navigate("delivery_package_screen")
                        } else {
                            Log.e("EntregarScreen", "Error al actualizar estado del envío")
                            Toast.makeText(
                                context,
                                "Pago registrado, pero no se pudo actualizar el estado del envío.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        viewModel.resetResultadoPago()
                    }
                } else {
                    Log.e("EntregarScreen", "envioActual es null al intentar actualizar estado del envío")
                    viewModel.resetResultadoPago()
                }
            }


            is ResultadoPago.Error -> {
                Log.e("EntregarScreen", "Error en el pago: ${resultado.mensaje}")
                Toast.makeText(context, "Error en el pago: ${resultado.mensaje}", Toast.LENGTH_LONG)
                    .show()
                // Decide si quieres cerrar el diálogo de efectivo o mantenerlo para reintentar
                // mostrarDialogoEfectivo = false // Comentado para que el usuario vea el error en el diálogo si aún está abierto
                viewModel.resetResultadoPago() // Resetea para permitir otro intento si es necesario
            }

            ResultadoPago.Loading -> {
                Log.d("EntregarScreen", "Procesando pago...")
                // No es necesario hacer nada aquí si el diálogo de efectivo ya tiene un indicador
                // o si se muestra un indicador de carga general (ver más abajo).
            }

            ResultadoPago.Idle -> {
                // Estado inicial o después de resetear, no se necesita acción específica aquí.
            }
        }
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
            valorRecaudar = envioData?.valorRecaudar?.toDouble() ?: 0.0,
            pagoRecibido = pagoRecibido,
            onPagoRecibidoChange = { pagoRecibido = it },
            onPagoExitoso = {
                viewModel.procesarPagoEfectivo()

            },
            onPagoErroneo = {
                mostrarDialogoEfectivo = false
                mostrarDialogoPayment = true
                Toast.makeText(
                    context,
                    "Operación cancelada o monto incorrecto.",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = { mostrarDialogoEfectivo = false }
        )
    }
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
                amount = envioData?.valorRecaudar?.toDouble() ?: 0.0,
                enabled = !envioEntregado,
                onRecaudarClick = {
                    if (envioEntregado) {
                        Toast.makeText(
                            context,
                            "Este envío ya fue entregado.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Recaudation
                    }

                    // El resto como ya lo tienes:
                    if (viewModel.envioActual != null && viewModel.idEmpresaB2B != null) {
                        mostrarDialogoPayment = true
                    } else {
                        Toast.makeText(
                            context,
                            "Cargando datos del envío, intente de nuevo en un momento.",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.w(
                            "EntregarScreen",
                            "Intento de Recaudar sin envioActual o idEmpresaB2B en ViewModel"
                        )
                    }
                }
            )
        }
        if (resultadoPagoState is ResultadoPago.Loading && !mostrarDialogoEfectivo && !mostrarDialogoTarjeta) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
                // Puedes añadir un fondo semi-transparente si quieres
                // .background(Color.Black.copy(alpha = 0.3f))
            ) {
                CircularProgressIndicator()
            }
        }
    }
}