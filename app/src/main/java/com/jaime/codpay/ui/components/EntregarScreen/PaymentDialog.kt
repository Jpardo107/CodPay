package com.jaime.codpay.ui.components.EntregarScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PaymentDialog(
    onDismiss: () -> Unit,
    onPagoTarjeta: () -> Unit,
    onPagoEfectivo: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = {
            Text(
                text = "Selecciona medio de pago",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column {
                Button(
                    onClick = onPagoTarjeta,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text("Tarjeta")
                }
                Button(
                    onClick = onPagoEfectivo,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text("Efectivo")
                }
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Cancelar")
                }
            }
        }
    )
}