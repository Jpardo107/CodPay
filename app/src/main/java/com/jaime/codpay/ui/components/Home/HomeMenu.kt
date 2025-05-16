package com.jaime.codpay.ui.components.Home

import android.graphics.Color.rgb
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Route
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeMenu(
    isRouteInitialized: Boolean,
    onCreateRuta: () -> Unit,
    onEntregar: () -> Unit,
    onVerResumen: () -> Unit,
    onVerRuta: () -> Unit,
    onResumenCobros: () -> Unit,
    onCerraRuta: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = Icons.Default.CheckCircle,
                text = "Iniciar ruta",
                backgroundColor = if (isRouteInitialized) Color.Gray else Color(
                    rgb(
                        27,
                        135,
                        84
                    )
                ), // Cambia color si está deshabilitado
                iconTint = Color.Black,
                textColor = Color.Black,
                onClick = onCreateRuta,
                enabled = !isRouteInitialized
            )
            ActionButton(
                icon = Icons.Default.Route,
                text = "Ver Ruta",
                backgroundColor = if (!isRouteInitialized) Color.Gray else Color.Black,
                onClick = onVerRuta,
                enabled = isRouteInitialized
            )
            ActionButton(
                icon = Icons.Default.DeliveryDining,
                text = "Entregar",
                backgroundColor = if (!isRouteInitialized) Color.Gray else Color.Black,
                onClick = onEntregar,
                enabled = isRouteInitialized
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = Icons.Default.FormatListNumbered,
                text = "Ver Resumen",
                backgroundColor = if (!isRouteInitialized) Color.Gray else Color.Black,
                onClick = onVerResumen,
                enabled = isRouteInitialized
            )
            ActionButton(
                icon = Icons.Default.ReceiptLong,
                text = "Resumen de cobros",
                backgroundColor = if (!isRouteInitialized) Color.Gray else Color.Black,
                onClick = onResumenCobros,
                enabled = isRouteInitialized
            )
            ActionButton(
                icon = Icons.Default.Lock,
                text = "Cerrar Ruta",
                backgroundColor = if (!isRouteInitialized) Color.Gray else Color(220, 53, 69),
                onClick = onCerraRuta,
                enabled = isRouteInitialized
            )
        }
    }
}