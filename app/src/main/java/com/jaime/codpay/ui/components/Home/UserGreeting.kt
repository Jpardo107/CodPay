package com.jaime.codpay.ui.components.Home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UserGreeting(userName: String, modifier: Modifier = Modifier){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hola $userName",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Excelente Jornada",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}