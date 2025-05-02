package com.jaime.codpay.Model

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage


@androidx.camera.core.ExperimentalGetImage
class BarcodeAnalizer(
    private val onCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer{
    private var alreadyScanned = false
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        val scanner = BarcodeScanning.getClient()
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val value = barcode.rawValue
                    if (!alreadyScanned && value != null) {
                        alreadyScanned = true
                        onCodeScanned(value)
                        break
                    }
                }
            }
            .addOnFailureListener {
                Log.e("QR_SCAN", "Error al escanear: ${it.localizedMessage}")
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}