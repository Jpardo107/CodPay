package com.jaime.codpay.Model

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@ExperimentalGetImage
class BarcodeAnalizer(private val onCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val scanner: BarcodeScanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull()?.let { barcode ->
                        barcode.rawValue?.let { rawValue ->
                            onCodeScanned(rawValue)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("QR_SCAN", "Error al analizar la imagen", it)
                }
                .addOnCompleteListener {
                    imageProxy.close() // 🔥 Liberar la imagen para que el flujo continúe
                }
        } else {
            imageProxy.close() // 🔥 Liberar la imagen si es nula
        }
    }
}