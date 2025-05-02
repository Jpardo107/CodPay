package com.jaime.codpay.ui.screens

import android.graphics.Color.rgb
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TwoFactorScreen(
    onAuthSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Autenticación de 2 pasos", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = code,
            onValueChange = {code = it},
            label = { Text("2do codigo") },
            leadingIcon = {
                Icon(Icons.Default.Message, contentDescription = "codigo")
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(rgb(231 ,231, 231)),
                unfocusedContainerColor = Color(rgb(208, 208, 208)),
                disabledContainerColor = Color(0xFFF6F6F6),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onAuthSuccess() },
            modifier = Modifier
                .width(200.dp)
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "check",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Aceptar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Reenviar código",
            color = Color.Blue,
            modifier = Modifier.clickable { /* Acción futura */ }
        )
        Spacer(modifier = Modifier.width(12.dp))
        TextButton(
            onClick = { onBackToLogin() },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "volver",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Volver",
                color = Color.DarkGray,
                fontSize = 14.sp
            )
        }
    }
}