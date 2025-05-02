package com.jaime.codpay.ui.components.EntregarScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun PagoEfectivoDialog(
    valorRecaudar: Double,
    pagoRecibido: String,
    onPagoRecibidoChange: (String) -> Unit,
    onPagoExitoso: () -> Unit,
    onPagoErroneo: () -> Unit,
    onDismiss: () -> Unit
) {
    val pagoRecibidoDouble = pagoRecibido.toDoubleOrNull() ?: 0.0
    val vuelto = pagoRecibidoDouble - valorRecaudar
    val pagoSuficiente = pagoRecibidoDouble >= valorRecaudar

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = {
            Text("Pago en efectivo", style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column {
                // Valor a recaudar (solo visual)
                Text(
                    text = "Valor a recaudar: $${"%.0f".format(valorRecaudar)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Campo de entrada
                OutlinedTextField(
                    value = pagoRecibido,
                    onValueChange = onPagoRecibidoChange,
                    label = { Text("Pago recibido") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Vuelto
                Text(
                    text = "Vuelto: $${"%.0f".format(if (vuelto > 0) vuelto else 0.0)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onPagoExitoso,
                        enabled = pagoSuficiente
                    ) {
                        Text("Pago exitoso")
                    }
                    OutlinedButton(onClick = onPagoErroneo) {
                        Text("Pago erróneo")
                    }
                }
            }
        }
    )
}
