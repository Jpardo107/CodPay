package com.jaime.codpay.ui.components.InitRoute

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScanSection(
    onScanClick: () -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.LightGray)
        ){
            Text(
                text = "Visor QR",
                color = Color.DarkGray,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Button(
            onClick = onScanClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            ),
            modifier = Modifier
                .height(80.dp)
                .width(120.dp),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = "Scan",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
