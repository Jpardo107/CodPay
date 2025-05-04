package com.jaime.codpay.ui.components.DeliveryPackage


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScanButton(onClick: () -> Unit){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .height(64.dp)
            .width(86.dp)
    ) {
        Text(text = "Scan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}




@Composable
fun QrPreviewBox(onQrScanned: (String) -> Unit){
    val context = LocalContext.current

    CameraPreview(
        onCodeScanned = { result ->
            onQrScanned(result) // Enviar el resultado al padre
            Log.d("QR_SCAN", "Código escaneado: $result")
            // Aquí podrías validar el código también
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 80.dp, vertical = 80.dp)
    )

}



@Composable
fun PedidoInput(pedido: String, onPedidoChange: (String) -> Unit){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(text = "N° Pedido", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = pedido,
            onValueChange = onPedidoChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}



@Composable
fun ActionButton(text: String, backgroundColor: Color, disablebgColor: Color, enabled: Boolean, onClick: () -> Unit){
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor, disabledContainerColor = disablebgColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(48.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text, color = Color.Black, fontWeight = FontWeight.Bold)
    }
}
