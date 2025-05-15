package com.jaime.codpay.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.jaime.codpay.data.Paquete
import com.jaime.codpay.data.QrData
import com.jaime.codpay.ui.components.DeliveryPackage.QrPreviewBox
import com.jaime.codpay.ui.components.InitRoute.ConfirmacionDialog
import com.jaime.codpay.ui.components.InitRoute.StartRouteButton
import com.jaime.codpay.ui.components.InitRoute.TitleSection
import com.jaime.codpay.ui.theme.CodPayTheme
import com.jaime.codpay.ui.viewmodel.PaquetesViewModel
import com.jaime.codpay.ui.viewmodel.PaquetesViewModelFactory
import com.jaime.codpay.ui.viewmodel.RutasViewModel
import com.jaime.codpay.ui.viewmodel.RutasViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InitRouteScreen(nombreRuta: String, navController: NavController) {

    val context = LocalContext.current
    val rutasViewModel: RutasViewModel = viewModel(factory = RutasViewModelFactory(context))
    val paquetesViewModel: PaquetesViewModel =
        viewModel(factory = PaquetesViewModelFactory(context, rutasViewModel.paquetesRepository))
    val rutas by rutasViewModel.rutas.collectAsState()
    val paquetes by paquetesViewModel.paquetes.collectAsState()
    var allPackagesScanned by remember { mutableStateOf(true) }
    var tieneSegundaRuta by remember { mutableStateOf(false) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    val isLoading by rutasViewModel.isLoading.collectAsState()
    val error by rutasViewModel.error.collectAsState()
    val gson = Gson()

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
                        try {
                            val qrData = gson.fromJson(qrCode, QrData::class.java)
                            paquetesViewModel.eliminarPaquete(qrData.idPaquete)
                            Log.d("QR_SCAN", "ID Paquete escaneado: ${qrData.idPaquete}")
                        } catch (e: Exception) {
                            Log.e("QR_SCAN", "Error al parsear el QR", e)
                            Toast.makeText(context, "Error al procesar el QR", Toast.LENGTH_SHORT).show()
                        }
                        Log.d("QR_SCAN", "QR leído: $qrCode")
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
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(paquetes, key = { item -> item.idPaquete }) { paquete ->
                    PackageItem(paquete = paquete, modifier = Modifier.animateItemPlacement())
                }
            }
        }

        StartRouteButton(
            allPackagesScanned = allPackagesScanned,
            onClick = { mostrarDialogo = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        if (mostrarDialogo) {
            ConfirmacionDialog(
                tieneSegundaRuta = tieneSegundaRuta,
                onCerrar = { mostrarDialogo = false },
                onContinuar = {
                    mostrarDialogo = false
                    navController.popBackStack()
                },
                onEscanearSegunda = {
                    mostrarDialogo = false
                },
                onVolver = {
                    mostrarDialogo = false
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun PackageItem(paquete: Paquete, modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(60.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFb0A5A6)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Código: ${paquete.codigoPaquete}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(text = "Descripción: ${paquete.descripcionPaquete}", fontSize = 16.sp)
        }
    }
}

//@OptIn(ExperimentalFoundationApi::class)
//@Preview(showBackground = true)
//@Composable
//fun InitRouteScreenPreview() {
//    Surface(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .height(60.dp),
//        shape = RoundedCornerShape(8.dp),
//        color = Color(0xFFb0A5A6)
//    ) {
//        Column(
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(text = "Cod: COD001", fontWeight = FontWeight.Bold, fontSize = 16.sp)
//            Text(text = "Descripcion: Televisor 40", fontSize = 16.sp)
//        }
//    }
//}