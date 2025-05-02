package com.jaime.codpay.ui.components.DeliveryPackage

import android.util.Log
import android.widget.LinearLayout
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.jaime.codpay.Model.BarcodeAnalizer

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(onCodeScanned: (String) -> Unit,
                  modifier: Modifier = Modifier
){
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val heightInPx = with(LocalDensity.current) { 80.dp.roundToPx() }
    // Referencia para controlar el PreviewView externamente
    var previewView: PreviewView? by remember { mutableStateOf(null) }
    var cameraProvider: ProcessCameraProvider? = null

    AndroidView(
        factory = { ctx ->
            val view = PreviewView(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    heightInPx
                )
            }

            previewView = view // guardar la referencia visible

            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(view.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(
                        ContextCompat.getMainExecutor(ctx),
                        BarcodeAnalizer(onCodeScanned)
                    )
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, analyzer
                )
            } catch (exc: Exception) {
                Log.e("QR_SCAN", "Error al iniciar la cámara", exc)
            }

            view
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
    DisposableEffect(Unit) {
        onDispose {
            try {
                previewView?.visibility = android.view.View.GONE // 🔥 oculta de inmediato
                cameraProvider?.unbindAll()
                Log.d("QR_SCAN", "Cámara liberada y Preview oculto")
            } catch (e: Exception) {
                Log.e("QR_SCAN", "Error al liberar la cámara", e)
            }
        }
    }
}