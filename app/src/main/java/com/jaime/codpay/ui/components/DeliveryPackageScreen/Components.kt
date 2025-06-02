package com.jaime.codpay.ui.components.DeliveryPackageScreen


import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PhotoCapture(
    modifier: Modifier = Modifier,
    onCaptureClick: () -> Unit,
    imageUri: Uri?,
    imageBitmap: Bitmap?
) {
    Log.d("PhotoCapture", "Recomponiendo PhotoCapture. imageBitmap is null: ${imageBitmap == null}")
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Tomar fotografia",
            modifier = modifier
                .size(80.dp)
                .padding(bottom = 16.dp),
            tint = Color.Gray
        )
        Text(
            text = "Capturar imagen de entrega",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCaptureClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(Color.Black)
        ) {
            Text(text = "Tomar foto", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (imageUri != null) {
            Log.d("PhotoCapture", "Mostrando imagen desde URI: $imageUri")
            AsyncImage(
                model = imageUri,
                contentDescription = "Imagen tomada",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // O ContentScale.Fit
            )
        }else if (imageBitmap != null) {
            Log.d("PhotoCapture", "Mostrando imagen desde Bitmap (preview)")
            Image(
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = "Vista previa de la imagen",
                modifier = Modifier.height(250.dp),
                contentScale = ContentScale.Crop // O ContentScale.Fit
            )
        }else{
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .background(color = Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Imagen tomada")
            }
        }

    }
}

@Composable
fun RecipientName(
    nombreReceptor: String,
    onNombreChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Receptor",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp),
            tint = Color.Gray
        )
        Text(
            text = "Nombre del receptor",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = nombreReceptor,
            onValueChange = onNombreChange,
            label = { Text("Nombre completo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FinalComment(
    comment: String,
    onCommentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = "Receptor",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp),
            tint = Color.Gray
        )
        Text(
            text = "Comentario adicional a la entrega",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = comment,
            onValueChange = onCommentChange,
            label = { Text("Escribe un comentario...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp), // alto visual tipo text area
            singleLine = false,
            maxLines = 6
        )
    }
}

