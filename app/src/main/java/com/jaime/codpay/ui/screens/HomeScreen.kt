package com.jaime.codpay.ui.screens

import android.app.Application
import android.util.Log
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
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
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner
    val userDataStore = remember { UserDataStore(context) }
    val rutasViewModel: RutasViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner, // USA EL MISMO OWNER
        factory = RutasViewModelFactory(context.applicationContext)
    )
    val paquetesViewModel: PaquetesViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner, // Si también debe ser compartido
        factory = PaquetesViewModelFactory(context.applicationContext, rutasViewModel.paquetesRepository)
    )
    val rutas by rutasViewModel.rutas.collectAsState()
    val paquetes by paquetesViewModel.paquetes.collectAsState()
    var messageShown by remember { mutableStateOf(false) }
    val userNameFlow: Flow<String?> = userDataStore.getUserName
    val userName by userNameFlow.collectAsState(initial = "")
    //var isRouteInitialized by remember { mutableStateOf(false) }
    val application = context.applicationContext as Application
    val coroutineScope = rememberCoroutineScope()

    // --- OBSERVAR EL ESTADO DE LA RUTA INICIALIZADA ---
    val isRouteInitialized by rutasViewModel.isRouteCurrentlyInitialized.collectAsState()
    Log.d(
        "HomeScreen_RutasVM",
        "HomeScreen observando isRouteInitialized: $isRouteInitialized desde RutasViewModel con HashCode: ${rutasViewModel.hashCode()}"
    )
    // --- FIN OBSERVAR ESTADO ---

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserGreeting(userName = userName ?: "Usuario")
        Button(
            onClick = {
                coroutineScope.launch {
                    // 1. Borrar datos del usuario
                    userDataStore.clearUserData()

                    // 2. Borrar datos de rutas
                    rutasViewModel.clearRutas()

                    // 3. Borrar datos de paquetes
                    paquetesViewModel.clearPaquetes()

                    // 4. Navegar a la pantalla de Login y limpiar el backstack
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop =
                            true // Evita múltiples instancias de Login si ya está en el backstack
                    }
                }
            },
            modifier = Modifier,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )
        ) {
            Text(text = "Cerrar sesion")
        }
        Spacer(modifier = Modifier.height(32.dp))
        HomeMenu(
            isRouteInitialized = isRouteInitialized, // Pasar el nuevo estado
            onCreateRuta = {
                val ruta = rutas[0]
                if (!isRouteInitialized) {
                    navController.navigate(Screen.InitRoute.createRoute(ruta.nombreRuta)) // Asumo que "Oriente" es un ejemplo
                }
            },
            onVerRuta = { navController.navigate(Screen.VerRuta.route) },
            onEntregar = { navController.navigate(Screen.Delivery.route) },
            onVerResumen = { /* Acción para ver resumen */ },
            onResumenCobros = { /* Acción para resumen de cobros */ },
            onCerraRuta = { /* Acción para cerrar ruta */ }
        )
        Spacer(modifier = Modifier.height(32.dp))
        // Mostrar datos dinámicos de la ruta
        if (rutas.isNotEmpty() && isRouteInitialized) {
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
        if (rutas.isNotEmpty()) {
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

