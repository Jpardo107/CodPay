package com.jaime.codpay.ui.screens

import android.net.Uri
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.jaime.codpay.ui.components.DeliveryPackageScreen.FinalComment
import com.jaime.codpay.ui.components.DeliveryPackageScreen.PhotoCapture
import com.jaime.codpay.ui.components.DeliveryPackageScreen.RecipientName
import com.jaime.codpay.ui.navigation.Screen
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import kotlin.text.format
import android.Manifest
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.IOException

//fun Context.createImageFile(): File {
//    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//    val imageFileName = "JPEG_" + timeStamp + "_"
//    val storageDir = getExternalFilesDir(null) // O Environment.DIRECTORY_PICTURES si lo cambiaste
//
//
//    if (storageDir == null) {
//        Log.e("createImageFile", "getExternalFilesDir es NULO. ¿Problema de almacenamiento o permisos?")
//        // Lanza una excepción o maneja este error críticamente,
//        // porque no puedes crear el archivo.
//        throw IOException("External storage directory is null")
//    }
//
//    if (!storageDir.exists()) {
//        val dirCreated = storageDir.mkdirs()
//        Log.d("createImageFile", "Directorio de almacenamiento no existía. ¿Creado?: $dirCreated - Path: ${storageDir.absolutePath}")
//        if (!dirCreated && !storageDir.exists()) { // Doble chequeo por si mkdirs falla silenciosamente
//            Log.e("createImageFile", "¡FALLO al crear el directorio de almacenamiento!: ${storageDir.absolutePath}")
//            throw IOException("Failed to create storage directory")
//        }
//    } else {
//        Log.d("createImageFile", "Directorio de almacenamiento ya existe: ${storageDir.absolutePath}")
//    }
//
//    Log.d("createImageFile", "Intentando crear archivo temporal en: ${storageDir.absolutePath} con nombre base: $imageFileName")
//
//    return try {
//        File.createTempFile(
//            imageFileName, /* prefix */
//            ".jpg",        /* suffix */
//            storageDir     /* directory */
//        ).apply {
//            Log.d("createImageFile", "ÉXITO al crear archivo temporal: ${this.absolutePath}, Tamaño: ${this.length()}")
//        }
//    } catch (ex: IOException) {
//        Log.e("createImageFile", "ERROR al crear archivo temporal", ex)
//        throw ex // Relanza para que el llamador sepa
//    }
//}

@OptIn(ExperimentalPagerApi::class, ExperimentalPermissionsApi::class)
@Composable
fun DeliveryPackageScreen(navController: NavController) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var imageBitmapState by remember { mutableStateOf<Bitmap?>(null) }
    var displayableImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingImageUri: Uri? = null
    var recipientNameText by rememberSaveable { mutableStateOf("") }
    var finalCommentText by rememberSaveable { mutableStateOf("") }
    val cameraPreviewLauncher = rememberLauncherForActivityResult( // Si decides usarlo
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap: Bitmap? ->
            if (bitmap != null) {
                imageBitmapState = bitmap
                displayableImageUri = null // Limpiar URI si usamos preview
                Log.d("DeliveryPackageScreen", "TakePicturePreview ÉXITO. Bitmap recibido.")
            } else {
                imageBitmapState = null
                Log.e("DeliveryPackageScreen", "TakePicturePreview FALLÓ. Bitmap nulo.")
            }
        }
    )

    var imageUriFromMediaStore by remember { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(), // SÍ, usamos TakePicture, pero el URI es de MediaStore
        onResult = { success ->
            if (success) {
                pendingImageUri?.let { uri ->
                    Log.d("DeliveryPackageScreen", "MediaStore TakePicture ÉXITO. URI: $uri")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.IS_PENDING, 0)
                        }
                        context.contentResolver.update(uri, contentValues, null, null)
                        Log.d("DeliveryPackageScreen", "Imagen finalizada en MediaStore: $uri")
                    }
                    displayableImageUri = uri // ¡AQUÍ es donde actualizas el URI para mostrar!
                    imageBitmapState = null // Limpiamos el bitmap de preview
                }
            } else {
                Log.e("DeliveryPackageScreen", "MediaStore TakePicture FALLÓ o fue cancelado.")
                pendingImageUri?.let { uri ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Si la creación del URI fue exitosa pero la cámara falló/canceló,
                        // el archivo pendiente en MediaStore (para Q+) debe ser eliminado.
                        context.contentResolver.delete(uri, null, null)
                        Log.d("DeliveryPackageScreen", "URI pendiente eliminado de MediaStore: $uri")
                    }
                }
                displayableImageUri = null // Asegúrate de limpiar si falla
            }
            pendingImageUri = null
        }
    )
    // Manejo de permisos: Cámara siempre, Almacenamiento condicional.
    val permissionsToRequest = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listOf(Manifest.permission.CAMERA)
        } else {
            listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissionsToRequest)

    fun capturePhotoWithMediaStore() {
        val name = "Entrega_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CodPayEntregas") // Guarda en Pictures/TuAppNombre
                put(MediaStore.Images.Media.IS_PENDING, 1) // Marcar como pendiente hasta que se escriba
            }
        }

        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val newImageUri = context.contentResolver.insert(imageCollection, contentValues)

        if (newImageUri != null) {
            pendingImageUri = newImageUri // Guarda el URI en la variable temporal
            Log.d("DeliveryPackageScreen", "URI de MediaStore creado (pendiente): $newImageUri. Lanzando cámara.")
            takePictureLauncher.launch(newImageUri)
        } else {
            Log.e("DeliveryPackageScreen", "Fallo al crear URI de MediaStore.")
            pendingImageUri = null
        }
    }
    fun finalizePendingImage(uri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            context.contentResolver.update(uri, contentValues, null, null)
            Log.d("DeliveryPackageScreen", "Imagen finalizada en MediaStore: $uri")
        }
    }



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            count = 3,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> PhotoCapture(
                    // Pasa el URI que se actualiza DESPUÉS de que la cámara confirma
                    imageUri = displayableImageUri,
                    imageBitmap = imageBitmapState, // Para la opción de TakePicturePreview
                    onCaptureClick = {
                        if (!multiplePermissionsState.allPermissionsGranted) {
                            if (multiplePermissionsState.shouldShowRationale) {
                                Log.i("DeliveryPackageScreen", "Mostrando justificación para permisos (deberías mostrar un diálogo).")
                                // Aquí es donde mostrarías un diálogo explicativo antes de pedir permisos.
                                // Por ahora, solo los pedimos.
                            }
                            multiplePermissionsState.launchMultiplePermissionRequest()
                        } else {
                            // Permisos concedidos, proceder a capturar
                            Log.d("DeliveryPackageScreen", "Permisos concedidos. Iniciando captura con TakePicturePreview.")
                            //capturePhotoWithMediaStore()
                            // O si prefieres dar la opción de preview (menos recomendado para imagen final):
                             cameraPreviewLauncher.launch()
                        }
                    }
                )
                1 -> RecipientName(
                    nombreReceptor = recipientNameText,
                    onNombreChange = { newName ->
                        recipientNameText = newName
                        // No se guarda nada más allá de este estado local
                    }
                )

                2 -> FinalComment(
                    comment = finalCommentText,
                    onCommentChange = { newComment ->
                        finalCommentText = newComment
                        // No se guarda nada más allá de este estado local
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage > 0) {
                Button(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color.Black)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Anterior"
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(120.dp))
            }
            if (pagerState.currentPage < 2) {
                Button(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color.Black)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Siguiente"
                    )
                }
            } else {
                Button(
                    onClick = {
                        navController.navigate(Screen.Home.route) // puedes cambiar esto por una pantalla de resumen o feedback
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Text("Finalizar")
                }
            }
        }
    }

}