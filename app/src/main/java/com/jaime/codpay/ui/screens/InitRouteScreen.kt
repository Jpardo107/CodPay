package com.jaime.codpay.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jaime.codpay.ui.components.InitRoute.BultosList
import com.jaime.codpay.ui.components.InitRoute.ConfirmacionDialog
import com.jaime.codpay.ui.components.InitRoute.ScanSection
import com.jaime.codpay.ui.components.InitRoute.StartRouteButton
import com.jaime.codpay.ui.components.InitRoute.TitleSection

@Composable
fun InitRouteScreen(nombreRuta: String, navController: NavController){
    var allPackagesScanned by remember { mutableStateOf(false) }
    var tieneSegundaRuta by remember { mutableStateOf(true) } // cambia a false para probar el otro caso
    var mostrarDialogo by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver al Home",
                tint = Color.Black
            )
        }

        TitleSection(nombreRuta)
        Spacer(modifier = Modifier.height(32.dp))
        ScanSection(onScanClick = {allPackagesScanned = !allPackagesScanned})
        Spacer(modifier = Modifier.height(32.dp))
        BultosList()

        StartRouteButton(
            allPackagesScanned = allPackagesScanned,
            onClick = {mostrarDialogo = true},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        if (mostrarDialogo) {
            ConfirmacionDialog(
                tieneSegundaRuta = tieneSegundaRuta,
                onCerrar = { mostrarDialogo = false },
                onContinuar = {
                    mostrarDialogo = false
                    navController.popBackStack() // simula ir al home
                },
                onEscanearSegunda = {
                    mostrarDialogo = false
                    // En el futuro: cargar lista de bultos 2da ruta
                },
                onVolver = {
                    mostrarDialogo = false
                    navController.popBackStack()
                }
            )
        }

    }
}
