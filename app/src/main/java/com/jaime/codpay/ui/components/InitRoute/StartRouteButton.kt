package com.jaime.codpay.ui.components.InitRoute

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StartRouteButton(
    allPackagesScanned: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (allPackagesScanned) Color.Black else Color.Gray,
        label = "ButtonBackgroundColor"
    )

    val alphaAnim by animateFloatAsState(
        targetValue = if (allPackagesScanned) 2f else 1f,
        label = "ButtonAlpha"
    )

    Button(
        onClick = onClick,
        enabled = allPackagesScanned,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .height(50.dp)
            .alpha(alphaAnim),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        )
    ) {
        Text(
            text = "Iniciar ruta",
            style = MaterialTheme.typography.labelLarge
        )
    }
}