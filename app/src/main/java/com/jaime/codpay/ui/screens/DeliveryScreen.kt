package com.jaime.codpay.ui.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jaime.codpay.ui.components.DeliveryPackage.ActionButton
import com.jaime.codpay.ui.components.DeliveryPackage.PedidoInput
import com.jaime.codpay.ui.components.DeliveryPackage.QrPreviewBox
import com.jaime.codpay.ui.components.DeliveryPackage.ScanButton
import com.jaime.codpay.ui.components.InitRoute.TitleSection
import com.jaime.codpay.ui.navigation.Screen
import com.jaime.codpay.utils.rememberCameraPermissionState

@Composable
fun DeliveryScreen(
    navController: NavController,
    onVolver: () -> Unit = {navController.popBackStack()},
    onEntregar: () -> Unit = {navController.navigate(Screen.Entregar.route)},
    onReagendar: () -> Unit = {},
    onRechazar: () -> Unit = {},
){
    var pedido by remember { mutableStateOf("") }
    val accionesHabilitadas = pedido.isNotBlank()
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
            if(cameraPermissionGranted) {
                QrPreviewBox(
                    onQrScanned = { qrCode ->
                        pedido = qrCode // Actualiza directamente el input
                    }
                )
            }

        }
        ScanButton(onClick = {
            pedido = "PED123456"
        })
        PedidoInput(
            pedido = pedido,
            onPedidoChange = { pedido = it }
        )
        ActionButton(
            text = "Entregar",
            backgroundColor = Color(27, 135, 84),
            disablebgColor = Color(171, 235, 198),
            enabled = accionesHabilitadas,
            onClick = onEntregar
        )

        ActionButton(
            text = "Reagendar",
            backgroundColor = Color(255, 193, 7),
            disablebgColor = Color(249, 231, 159),
            enabled = accionesHabilitadas,
            onClick = onReagendar,
        )

        ActionButton(
            text = "Rechazar entrega",
            backgroundColor = Color(220, 53, 69),
            disablebgColor = Color(245, 183, 177),
            enabled = accionesHabilitadas,
            onClick = onRechazar
        )
        Button(onClick = onVolver, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )) {
            Text("Volver")
        }
    }
}