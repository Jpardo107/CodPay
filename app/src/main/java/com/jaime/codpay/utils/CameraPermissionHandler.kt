package com.jaime.codpay.utils

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun rememberCameraPermissionState(): State<Boolean> {
    val context = LocalContext.current
    val permission = Manifest.permission.CAMERA
    val grantedInitially = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    var isGranted by remember { mutableStateOf(grantedInitially) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        isGranted = granted
        if (!granted) {
            Toast.makeText(context, "Permiso de cámara requerido", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!grantedInitially) {
            launcher.launch(permission)
        }
    }

    return rememberUpdatedState(isGranted)
}
