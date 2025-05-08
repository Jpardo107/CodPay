package com.jaime.codpay.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.jaime.codpay.data.Envio
//import com.jaime.codpay.data.Pedido
import com.jaime.codpay.ui.components.DeliveryPackage.ActionButton
import com.jaime.codpay.ui.components.DeliveryPackage.PedidoInput
import com.jaime.codpay.ui.components.DeliveryPackage.QrPreviewBox
import com.jaime.codpay.ui.components.InitRoute.TitleSection
import com.jaime.codpay.ui.navigation.Screen
import com.jaime.codpay.ui.viewmodel.EnviosViewModel
import com.jaime.codpay.ui.viewmodel.EnviosViewModelFactory
import com.jaime.codpay.ui.viewmodel.EnviosViewModelFactoryDelivery
import java.util.concurrent.Executors

@Composable
fun DeliveryScreen(
    navController: NavController,
    onVolver: () -> Unit = { navController.popBackStack() }
) {
    val gson = Gson()
    var pedido by remember { mutableStateOf("") }
    val accionesHabilitadas = pedido.isNotBlank()
    val context = LocalContext.current
    val enviosViewModel: EnviosViewModel = viewModel(factory = EnviosViewModelFactoryDelivery(context))
    val envios by enviosViewModel.envios.collectAsState()
    var envioEncontrado by remember { mutableStateOf<Envio?>(null) }

    LaunchedEffect(key1 = true){
        enviosViewModel.getEnvios()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleSection(nombre = "Entregar")
        val cameraPermissionGranted by rememberCameraPermissionState()
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (cameraPermissionGranted) {
                QrPreviewBox(
                    onQrScanned = { qrCode ->
                        pedido = qrCode
                        try {
                            val gson = Gson()
                           val jsonObject = gson.fromJson(qrCode, JsonObject::class.java)
                            val idEnvio = jsonObject.get("idEnvio").asInt
                            Log.d("DeliveryScreen", "idEnvio escaneado: $idEnvio") //
                            pedido = jsonObject.get("numeroRefPedidoB2C").asString
                            envioEncontrado = enviosViewModel.getEnvioByIdEnvio(idEnvio)
                            Log.d("DeliveryScreen", "Pedido Encontrado: $envioEncontrado")
                            if (envioEncontrado == null) {
                                Toast.makeText(context, "Envío no encontrado", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error al procesar el QR", Toast.LENGTH_SHORT).show()
                            envioEncontrado = null
                        }
                    }
                )
            }

        }
        PedidoInput(
            pedido = pedido,
            onPedidoChange = {
                pedido = it
            }
        )
        ActionButton(
            text = "Entregar",
            backgroundColor = Color(27, 135, 84),
            disablebgColor = Color(171, 235, 198),
            enabled = accionesHabilitadas,
            onClick = {
                if (envioEncontrado != null) {
                    val gson = Gson()
                    val envioJson = gson.toJson(envioEncontrado)
                    navController.navigate("entregar_screen/$envioJson")
                } else {
                    Toast.makeText(context, "Escanee un QR primero", Toast.LENGTH_SHORT).show()
                }
            }
        )

        ActionButton(
            text = "Reagendar",
            backgroundColor = Color(255, 193, 7),
            disablebgColor = Color(249, 231, 159),
            enabled = accionesHabilitadas,
            onClick = {
                if (envioEncontrado != null) {
                    val gson = Gson()
                    val envioJson = gson.toJson(envioEncontrado)
                    navController.navigate("reagendar_screen/$envioJson")
                } else {
                    Toast.makeText(context, "Escanee un QR primero", Toast.LENGTH_SHORT).show()
                }
            },
        )

        ActionButton(
            text = "Rechazar entrega",
            backgroundColor = Color(220, 53, 69),
            disablebgColor = Color(245, 183, 177),
            enabled = accionesHabilitadas,
            onClick = {
                if (envioEncontrado != null) {
                    val gson = Gson()
                    val envioJson = gson.toJson(envioEncontrado)
                    navController.navigate("rechazar_screen/$envioJson")
                } else {
                    Toast.makeText(context, "Escanee un QR primero", Toast.LENGTH_SHORT).show()
                }
            }
        )
        Button(
            onClick = onVolver,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            )
        ) {
            Text("Volver")
        }
    }
    
}

@Composable
fun rememberCameraPermissionState(): androidx.compose.runtime.State<Boolean> {
    val context = LocalContext.current
    var cameraPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            cameraPermissionGranted = granted
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }
    return remember { mutableStateOf(cameraPermissionGranted) }
}