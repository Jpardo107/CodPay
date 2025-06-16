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
import androidx.compose.runtime.derivedStateOf
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
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.jaime.codpay.data.EnviosRepository
import com.jaime.codpay.data.EnviosRepositoryImpl
import com.jaime.codpay.data.Paquete
import com.jaime.codpay.data.PaqueteConEnvio
import com.jaime.codpay.data.QrData
import com.jaime.codpay.data.RetrofitClient
import com.jaime.codpay.data.RutaDataStore
import com.jaime.codpay.data.UserDataStore
import com.jaime.codpay.ui.components.DeliveryPackage.QrPreviewBox
import com.jaime.codpay.ui.components.InitRoute.ConfirmacionDialog
import com.jaime.codpay.ui.components.InitRoute.StartRouteButton
import com.jaime.codpay.ui.components.InitRoute.TitleSection
import com.jaime.codpay.ui.theme.CodPayTheme
import com.jaime.codpay.ui.viewmodel.EnviosViewModel
import com.jaime.codpay.ui.viewmodel.EnviosViewModelFactory
import com.jaime.codpay.ui.viewmodel.PaquetesViewModel
import com.jaime.codpay.ui.viewmodel.PaquetesViewModelFactory
import com.jaime.codpay.ui.viewmodel.RutasViewModel
import com.jaime.codpay.ui.viewmodel.RutasViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InitRouteScreen(nombreRuta: String, navController: NavController) {

    val context = LocalContext.current
    val viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner
    val rutasViewModel: RutasViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner, // Especifica el owner
        factory = RutasViewModelFactory(context.applicationContext)
    )
    val paquetesViewModel: PaquetesViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = PaquetesViewModelFactory(context.applicationContext, rutasViewModel.paquetesRepository)
    )
    val rutas by rutasViewModel.rutas.collectAsState()
    val paquetes by paquetesViewModel.paquetes.collectAsState()
    //var allPackagesScanned by remember { mutableStateOf(true) }
    var tieneSegundaRuta by remember { mutableStateOf(false) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    val isLoading by rutasViewModel.isLoading.collectAsState()
    val error by rutasViewModel.error.collectAsState()
    val gson = Gson()

    val paquetesPendientes by paquetesViewModel.paquetes.collectAsState()
    val paquetesPendientesConEnvio by paquetesViewModel.paquetesConEnvio.collectAsState()

    val initialPackageCountLoaded = remember { mutableStateOf(false) }
    val initialPackageCount = remember { mutableStateOf(0) }

    val cameraPermissionGranted by rememberCameraPermissionState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val userDataStore = UserDataStore(context)
    val rutaDataStore = RutaDataStore(context)
    val enviosRepository = EnviosRepositoryImpl()

    val enviosViewModel: EnviosViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = EnviosViewModelFactory(context = context, enviosRepository, userDataStore, rutaDataStore )
    )

    val envios by enviosViewModel.envios.collectAsState()
    val paquetesConEnvio by paquetesViewModel.paquetesConEnvio.collectAsState()

    LaunchedEffect(Unit) {
        enviosViewModel.getEnvios()
    }

    LaunchedEffect(envios) {
        if (envios.isNotEmpty()) {
            paquetesViewModel.setEnvios(envios)
        }
    }



//    LaunchedEffect(rutas) {
//        if (envios.isNotEmpty()) {
//            paquetesViewModel.setEnvios(envios)
//        }
//    }

    LaunchedEffect(paquetesPendientesConEnvio) {
        if (!initialPackageCountLoaded.value && paquetesPendientesConEnvio.isNotEmpty()) {
            initialPackageCount.value = paquetesPendientesConEnvio.size
            initialPackageCountLoaded.value = true
            Log.d("InitRoute_Debug", "Conteo inicial establecido: ${initialPackageCount.value}")
        }
    }


    val allPackagesScanned by remember(
        initialPackageCountLoaded.value,
        initialPackageCount.value,
        paquetesPendientesConEnvio
    ) {
        derivedStateOf {
            val countIsLoaded = initialPackageCountLoaded.value
            val initialCount = initialPackageCount.value
            val pendientesEmpty = paquetesPendientesConEnvio.isEmpty()
            Log.d("InitRoute_Debug", "allPackagesScanned Check: Loaded=$countIsLoaded, InitialCount=$initialCount, PendientesEmpty=$pendientesEmpty")

            val result = countIsLoaded && initialCount > 0 && pendientesEmpty
            Log.d("InitRoute_Debug", "allPackagesScanned Result: $result")
            result
        }
    }

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
                            paquetesViewModel.eliminarPaqueteConEnvio(qrData.idPaquete)
                            Log.d("QR_SCAN", "ID Paquete escaneado: ${qrData.idPaquete}")
                        } catch (e: Exception) {
                            Log.e("QR_SCAN", "Error al parsear el QR", e)
                            Toast.makeText(context, "Error al procesar el QR", Toast.LENGTH_SHORT)
                                .show()
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
                items(paquetesConEnvio, key = { item -> item.idPaquete }) { paqueteConEnvio ->
                    PaqueteConEnvioItem(paqueteConEnvio = paqueteConEnvio, modifier = Modifier.animateItemPlacement())
                }
            }
        }

        StartRouteButton(
            allPackagesScanned = allPackagesScanned,
            onClick = {
                if (allPackagesScanned) {
                    mostrarDialogo = true
                } else {
                    Toast.makeText(
                        context,
                        "Escanea todos los paquetes primero.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        if (mostrarDialogo) {
            ConfirmacionDialog(
                tieneSegundaRuta = tieneSegundaRuta,
                onCerrar = { mostrarDialogo = false },
                onContinuar = {
                    mostrarDialogo = false
                    rutasViewModel.markRouteAsInitialized()
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

//@Composable
//fun PackageItem(PaqueteConEnvio: paqueteConEnvio, modifier: Modifier = Modifier) {
//    Surface(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .height(60.dp),
//        shape = RoundedCornerShape(8.dp),
//        color = Color(0xFFb0A5A6)
//    ) {
//        Column(
//            verticalArrangement = Arrangement.SpaceEvenly,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "Código B2C: ${paquete.codigoPaquete}",
//                fontWeight = FontWeight.Bold,
//                fontSize = 18.sp
//            )
//            Text(text = "Descripción: ${paquete.descripcionPaquete}", fontSize = 16.sp)
//        }
//    }
//}

@Composable
fun PaqueteConEnvioItem(paqueteConEnvio: PaqueteConEnvio, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(120.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFb0A5A6)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Codigo B2C: ${paqueteConEnvio.numeroRefPedidoB2C}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(text = "Cliente B2C: ${paqueteConEnvio.nombreClienteB2C}", fontSize = 16.sp)
            //Text(text = "Cliente B2C: Default", fontSize = 16.sp)
//            Text(text = "Cliente: ${paqueteConEnvio.clienteNombre}", fontSize = 14.sp)
//            Text(text = "Dirección: ${paqueteConEnvio.direccionEntrega}, ${paqueteConEnvio.comunaEntrega}", fontSize = 14.sp)
//            Text(text = "Pedido B2C: ${paqueteConEnvio.numeroRefPedidoB2C} - ClienteB2C: ${paqueteConEnvio.idClienteB2C}", fontSize = 14.sp)
        }
    }
}

