package com.jaime.codpay.ui.components.WatchRoute

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RouteItem(
    cliente: String,
    direccion: String,
    comuna: String,
    pedido: Int,
    onIrClick: () -> Unit,
    onMoveUpClick : () -> Unit,
    onMoveDownClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Cliente",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = cliente,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Dirección",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$direccion, $comuna",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                }
                Spacer(modifier = Modifier.height(16.dp))
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Archive,
                        contentDescription = "Paquete",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pedido N°: $pedido",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = onIrClick,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 60.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color.Black)
                ) {
                    Text("Ir")
                }
                Row{
                    IconButton(onClick = onMoveUpClick) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Arriba"
                        )
                    }
                    IconButton(onClick = onMoveDownClick) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Abajo"
                        )
                    }
                }
            }
        }
    }
}