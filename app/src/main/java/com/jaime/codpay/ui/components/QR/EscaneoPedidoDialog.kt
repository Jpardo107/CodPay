package com.jaime.codpay.ui.components.QR

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jaime.codpay.ui.components.DeliveryPackage.QrPreviewBox

enum class ModoEscaneo {
    AGREGAR,
    QUITAR
}

@Composable
fun EscaneoPedidoDialog(
    modo: ModoEscaneo,
    onClose: () -> Unit,
    onQrDetected: (qrData: String) -> Unit,
    progreso: String? = null,
    onConfirm: (() -> Unit)? = null, // <-- nuevo parámetro opcional
    confirmEnabled: Boolean = false // <-- nuevo parámetro opcional
) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (modo == ModoEscaneo.AGREGAR) "Escanear pedido para agregar" else "Escanear pedido para quitar",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                progreso?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                QrPreviewBox(onQrScanned = onQrDetected)

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Confirmar SOLO si onConfirm != null
                onConfirm?.let {
                    Button(
                        onClick = it,
                        enabled = confirmEnabled,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text("Confirmar pedidos agregados")
                    }
                }

                // Botón Cancelar siempre
                Button(onClick = onClose) {
                    Text("Cancelar")
                }
            }
        }
    }
}

