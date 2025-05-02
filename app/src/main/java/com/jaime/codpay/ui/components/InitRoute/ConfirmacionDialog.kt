package com.jaime.codpay.ui.components.InitRoute

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmacionDialog(
    tieneSegundaRuta: Boolean,
    onCerrar: () -> Unit,
    onContinuar: () -> Unit,
    onEscanearSegunda: () -> Unit = {},
    onVolver: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onVolver,
        title = {
            Text(
                text = if (tieneSegundaRuta) "Tiene otra ruta asignada" else "Todos los bultos han sido escaneados",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            if (tieneSegundaRuta) {
                Text(
                    "¿Qué desea hacer a continuación?",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (tieneSegundaRuta) {
                    TextButton(onClick = onEscanearSegunda) {
                        Text("Escanear 2da ruta")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onContinuar) {
                        Text("Continuar con ruta actual")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onVolver) {
                        Text("Volver")
                    }
                } else {
                    TextButton(onClick = onContinuar) {
                        Text("Aceptar")
                    }
                }
            }
        },
        containerColor = Color.White,
        titleContentColor = Color.Black,
        textContentColor = Color.DarkGray,
        shape = RoundedCornerShape(16.dp)
    )
}