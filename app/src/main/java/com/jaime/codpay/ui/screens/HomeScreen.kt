package com.jaime.codpay.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jaime.codpay.data.UserDataStore
import com.jaime.codpay.ui.components.Home.HomeMenu
import com.jaime.codpay.ui.components.Home.RoutePieChart
import com.jaime.codpay.ui.components.Home.RouteSumaryCard
import com.jaime.codpay.ui.components.Home.UserGreeting
import com.jaime.codpay.ui.navigation.Screen
import com.jaime.codpay.ui.viewmodel.PaquetesViewModel
import com.jaime.codpay.ui.viewmodel.PaquetesViewModelFactory
import com.jaime.codpay.ui.viewmodel.RutasViewModel
import com.jaime.codpay.ui.viewmodel.RutasViewModelFactory
import kotlinx.coroutines.flow.Flow

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val userDataStore = UserDataStore(context)
    val rutasViewModel: RutasViewModel = viewModel(factory = RutasViewModelFactory(context))
    val paquetesViewModel: PaquetesViewModel = viewModel(factory = PaquetesViewModelFactory(context, rutasViewModel.paquetesRepository))
    val rutas by rutasViewModel.rutas.collectAsState()
    val paquetes by paquetesViewModel.paquetes.collectAsState()
    var messageShown by remember { mutableStateOf(false) }
    val userNameFlow: Flow<String?> = userDataStore.getUserName
    val userName by userNameFlow.collectAsState(initial = "")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        UserGreeting(userName = userName ?: "Usuario")
        Spacer(modifier = Modifier.height(32.dp))
        HomeMenu(
            onCreateRuta = {
                navController.navigate(Screen.InitRoute.createRoute("Oriente"))
            },
            onVerRuta = { navController.navigate(Screen.VerRuta.route) },
            onEntregar = { navController.navigate(Screen.Delivery.route) },
            onVerResumen = { /* Acción para ver resumen */ },
            onResumenCobros = { /* Acción para resumen de cobros */ },
            onCerraRuta = { /* Acción para cerrar ruta */ }
        )
        Spacer(modifier = Modifier.height(32.dp))
        // Mostrar datos dinámicos de la ruta
        if (rutas.isNotEmpty()) {
            val ruta = rutas[0] // Tomamos la primera ruta (asumiendo que solo hay una)
            RouteSumaryCard(
                routeName = ruta.nombreRuta,
                totalBultos = ruta.idEnvio.size,
                entregados = 0,
                reagendados = 0,
                cancelados = 0
            )
            messageShown = false
        } else {
            if (!messageShown) {
                Log.d("HomeScreen", "No hay rutas disponibles")
                messageShown = true
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        if(rutas.isNotEmpty()){
            val ruta = rutas[0]
            RoutePieChart(
                totalBultos = ruta.idEnvio.size,
                entregados = 0,
                reagendados = 0,
                cancelados = 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 24.dp)
            )
        }
    }
}