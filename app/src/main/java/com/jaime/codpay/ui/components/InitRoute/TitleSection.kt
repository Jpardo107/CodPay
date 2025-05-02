package com.jaime.codpay.ui.components.InitRoute

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun TitleSection(nombre: String){
    Text(
        text = "$nombre",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}