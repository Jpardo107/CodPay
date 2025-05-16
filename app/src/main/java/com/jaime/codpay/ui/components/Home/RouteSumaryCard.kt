package com.jaime.codpay.ui.components.Home

import android.graphics.Color.rgb
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RouteSumaryCard(
    routeName: String,
    totalBultos: Int,
    entregados: Int,
    reagendados: Int,
    cancelados: Int,
    modifier: Modifier = Modifier
){
    val porEntregar = totalBultos - (entregados + reagendados + cancelados)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(rgb(226 ,226, 226))),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp))
        {
            Text(
                text = routeName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            RouteStatRow(label = "Total Envios", value = totalBultos.toString())
            RouteStatRow(label = "Entregados", value = entregados.toString())
            RouteStatRow(label = "Reagendados", value = reagendados.toString())
            RouteStatRow(label = "Cancelados", value = cancelados.toString())
            RouteStatRow(label = "Por Entregar", value = porEntregar.toString())
        }
    }
}

@Composable
private fun RouteStatRow(label: String, value: String){
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}