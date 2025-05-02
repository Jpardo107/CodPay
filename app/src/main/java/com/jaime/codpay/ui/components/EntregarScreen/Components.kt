package com.jaime.codpay.ui.components.EntregarScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DeliveryHeaderInfo(
    fecha: String, horaIncio: String, horaFin: String, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = fecha, style = MaterialTheme.typography.labelSmall, color = Color.Blue)
                Text(text = horaIncio, fontWeight = FontWeight.Bold)
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Rango Horario",
                tint = Color.Gray
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = fecha, style = MaterialTheme.typography.labelSmall, color = Color.Blue)
                Text(text = horaFin, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdressDelivery(
    modifier: Modifier = Modifier,
    direccionCliente: String,
    regionCliente: String,
    comunaCliente: String,
    onMaps: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            Text(
                text = "Direccion de entrega",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = modifier.height(4.dp))
            Text(
                text = "$direccionCliente, $regionCliente, $comunaCliente",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = modifier.height(4.dp))
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onMaps) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Ver en mapa",
                        tint = Color.Blue,
                        modifier = modifier.size(16.dp)
                    )
                    Spacer(modifier = modifier.width(4.dp))
                    Text(text = "Mapa", color = Color.Blue, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun AditionalCommment(
    modifier: Modifier = Modifier,
    comentario: String
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            Text(
                text = "Informacion adicional",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = modifier.height(4.dp))
            Text(
                text = "$comentario",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun Recaudation(
    modifier: Modifier = Modifier,
    amount: Double,
    onRecaudarClick: ()-> Unit
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            Text(
                text = "Monto a recaudar",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$amount",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onRecaudarClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color(27, 135, 84))
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Cobrar"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Cobrar", fontWeight = FontWeight.Bold)
                }
            }

        }
    }
}

@Composable
fun RecipientCard(
    nombreDestinatario: String,
    onLlamarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            Text(
                text = "Destinatario",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = modifier.height(4.dp))
            Text(
                text = nombreDestinatario,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = modifier.height(8.dp))
            Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onLlamarClick) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Llamar",
                        tint = Color.Blue,
                        modifier = modifier.size(16.dp)
                    )
                    Spacer(modifier = modifier.width(4.dp))
                    Text(text = "Llamar", color = Color.Blue, fontSize = 14.sp)
                }

            }
        }
    }
}