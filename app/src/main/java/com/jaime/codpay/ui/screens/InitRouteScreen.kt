package com.jaime.codpay.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jaime.codpay.ui.components.DeliveryPackage.QrPreviewBox
import com.jaime.codpay.ui.components.InitRoute.RouteItem
import com.jaime.codpay.ui.components.InitRoute.ConfirmacionDialog
import com.jaime.codpay.ui.components.InitRoute.StartRouteButton
import com.jaime.codpay.ui.components.InitRoute.TitleSection
import com.jaime.codpay.ui.theme.CodPayTheme
import com.jaime.codpay.ui.viewmodel.RutasViewModel
import com.jaime.codpay.ui.viewmodel.RutasViewModelFactory
import com.jaime.codpay.utils.AbrirNavegacion
import com.jaime.codpay.utils.swap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InitRouteScreen(nombreRuta: String, navController: NavController){


    val context = LocalContext.current
    val rutasViewModel: RutasViewModel = viewModel(factory = RutasViewModelFactory(context))
    val rutas by rutasViewModel.rutas.collectAsState()
    var allPackagesScanned by remember { mutableStateOf(false) }
    var tieneSegundaRuta by remember { mutableStateOf(true) } // cambia a false para probar el otro caso
    var mostrarDialogo by remember { mutableStateOf(false) }
    val isLoading by rutasViewModel.isLoading.collectAsState()
    val error by rutasViewModel.error.collectAsState()

    val cameraPermissionGranted by rememberCameraPermissionState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver al Home",
                tint = Color.Black
            )
        }

        TitleSection(nombreRuta)
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (cameraPermissionGranted) {
                QrPreviewBox(
                    onQrScanned = { qrCode ->
                        Log.d("QR_SCAN", "QR leído: $qrCode")
//                        pedido = qrCode
//                        try {
//                            val gson = Gson()
//                            pedidoData = gson.fromJson(qrCode, Pedido::class.java)
//                        } catch (e: Exception) {
//                            Toast.makeText(context, "Error al procesar el QR", Toast.LENGTH_SHORT).show()
//                            pedidoData = null
//                        }
                    }
                )
            }

        }
        Spacer(modifier = Modifier.height(32.dp))
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else if (error != null) {
            LaunchedEffect(key1 = error) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        } else if (rutas.isEmpty()) {
            Text(
                text = "No hay rutas disponibles",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Para que ocupe el espacio disponible
                contentPadding = PaddingValues(16.dp), // Padding de la lista
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre items
            ) {
                itemsIndexed(rutas, key = { _, item -> item.idEnvio }) { index, ruta ->
                    RouteItem(
//                        cliente = ruta.nombreClienteFinal,
//                        direccion = ruta.direccionEntrega,
//                        comuna = ruta.comunaEntrega,
//                        pedido = ruta.numeroRefPedidoB2C,
                        cliente = "ruta.nombreClienteFinal",
                        direccion = "ruta.direccionEntrega",
                        comuna = "ruta.comunaEntrega",
                        pedido = "ruta.numeroRefPedidoB2C",
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }

        StartRouteButton(
            allPackagesScanned = allPackagesScanned,
            onClick = {mostrarDialogo = true},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        if (mostrarDialogo) {
            ConfirmacionDialog(
                tieneSegundaRuta = tieneSegundaRuta,
                onCerrar = { mostrarDialogo = false },
                onContinuar = {
                    mostrarDialogo = false
                    navController.popBackStack() // simula ir al home
                },
                onEscanearSegunda = {
                    mostrarDialogo = false
                    // En el futuro: cargar lista de bultos 2da ruta
                },
                onVolver = {
                    mostrarDialogo = false
                    navController.popBackStack()
                }
            )
        }

    }
}

