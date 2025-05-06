package com.jaime.codpay.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jaime.codpay.ui.components.Home.HomeMenu
import com.jaime.codpay.ui.components.Home.RoutePieChart
import com.jaime.codpay.ui.components.Home.RouteSumaryCard
import com.jaime.codpay.ui.components.Home.UserGreeting
import com.jaime.codpay.ui.navigation.Screen
import com.jaime.codpay.ui.viewmodel.RutasViewModel
import com.jaime.codpay.ui.viewmodel.RutasViewModelFactory

@Composable
fun HomeScreen(navController: NavController) {
    // Contenido principal
    /** Acción para resumen de cobros */
    /** Acción para resumen de cobros */
    /** Acción para ver resumen */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        UserGreeting(userName = "Jaime")
        Spacer(modifier = Modifier.height(32.dp))
        HomeMenu(
            onCreateRuta = {
                navController.navigate(Screen.InitRoute.createRoute("Oriente"))
            },
            onVerRuta = { navController.navigate(Screen.VerRuta.route) },
            onEntregar = { navController.navigate(Screen.Delivery.route) },
            onVerResumen = { /** Acción para ver resumen */ },
            onResumenCobros = { /** Acción para resumen de cobros */ },
            onCerraRuta = { /** Acción para resumen de cobros */ }
        )
        Spacer(modifier = Modifier.height(32.dp))
        RouteSumaryCard(
            routeName = "Ruta Oriente",
            totalBultos = 50,
            entregados = 20,
            reagendados = 5,
            cancelados = 5
        )

        Spacer(modifier = Modifier.height(32.dp))

        RoutePieChart(
            totalBultos = 50,
            entregados = 20,
            reagendados = 5,
            cancelados = 5,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 24.dp)
        )
    }
}