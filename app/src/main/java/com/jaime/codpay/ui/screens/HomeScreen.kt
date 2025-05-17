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
import androidx.compose.runtime.LaunchedEffect
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
import com.jaime.codpay.data.RutaDataStore
import com.jaime.codpay.data.UserDataStore
import com.jaime.codpay.ui.components.Home.HomeMenu
import com.jaime.codpay.ui.components.Home.RoutePieChart
import com.jaime.codpay.ui.components.Home.RouteSumaryCard
import com.jaime.codpay.ui.components.Home.UserGreeting
import com.jaime.codpay.ui.navigation.Screen
import com.jaime.codpay.ui.viewmodel.EnviosViewModel
import com.jaime.codpay.ui.viewmodel.EnviosViewModelFactoryDelivery
import com.jaime.codpay.ui.viewmodel.PaquetesViewModel
import com.jaime.codpay.ui.viewmodel.PaquetesViewModelFactory
import com.jaime.codpay.ui.viewmodel.RutasViewModel
import com.jaime.codpay.ui.viewmodel.RutasViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val rutaDataStore = remember { RutaDataStore(context) }
    val viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner
    val userDataStore = remember { UserDataStore(context) }
    val rutasViewModel: RutasViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner, // USA EL MISMO OWNER
        factory = RutasViewModelFactory(context.applicationContext)
    )
    val paquetesViewModel: PaquetesViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner, // Si también debe ser compartido
        factory = PaquetesViewModelFactory(
            context.applicationContext,
            rutasViewModel.paquetesRepository
        )
    )
    val enviosViewModel: EnviosViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner, // Mismo owner para compartir scope si es necesario
        factory = EnviosViewModelFactoryDelivery(application) // Usar application
    )
    val rutas by rutasViewModel.rutas.collectAsState()
    val paquetes by paquetesViewModel.paquetes.collectAsState()
    var messageShown by remember { mutableStateOf(false) }
    val userNameFlow: Flow<String?> = userDataStore.getUserName
    val userName by userNameFlow.collectAsState(initial = "")
    val coroutineScope = rememberCoroutineScope()
    val enviosDeRutaActiva by enviosViewModel.envios.collectAsState()
    val isLoadingEnvios by enviosViewModel.isLoading.collectAsState()
    val errorEnvios by enviosViewModel.error.collectAsState()

    // --- OBSERVAR EL ESTADO DE LA RUTA INICIALIZADA ---
    val isRouteInitialized by rutasViewModel.isRouteCurrentlyInitialized.collectAsState()
    Log.d(
        "HomeScreen_RutasVM",
        "HomeScreen observando isRouteInitialized: $isRouteInitialized desde RutasViewModel con HashCode: ${rutasViewModel.hashCode()}"
    )
    // --- FIN OBSERVAR ESTADO ---
    Log.d(
        "HomeScreen_EnviosVM",
        "Envios cargados: ${enviosDeRutaActiva.size}, Loading: $isLoadingEnvios (EnviosVM Hash: ${enviosViewModel.hashCode()})"
    )
    LaunchedEffect(isRouteInitialized) {
        if (isRouteInitialized) {
            Log.d("HomeScreen", "Ruta inicializada, llamando a enviosViewModel.getEnvios()")
            enviosViewModel.getEnvios() // EnviosViewModel internamente usa RutaDataStore para el idRuta
        } else {
            // Opcional: Limpiar envíos si la ruta se cierra, aunque EnviosViewModel podría no tener una función de limpiar explícita
            // y simplemente devolvería lista vacía si idRuta en DataStore es null/inválido.
            Log.d("HomeScreen", "Ruta no inicializada, no se cargan envíos específicos de ruta.")
        }
    }

    // Calcular estadísticas de envíos
    val totalEnviosEnRuta = enviosDeRutaActiva.size
    val entregados =
        enviosDeRutaActiva.count { it.estadoEnvio == "Entregado" } // Ajusta "ENTREGADO" al valor real
    val reagendados =
        enviosDeRutaActiva.count { it.estadoEnvio == "Reprogramado" } // Ajusta "REAGENDADO"
    val cancelados = enviosDeRutaActiva.count { it.estadoEnvio == "Fallido" } // Ajusta




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

                    // Borrar datos de rutas y envios
                    rutaDataStore.clearRutas()

                    // Borrar datos de rutas en viewModel
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
                Log.d("HomeScreen_Nav", "onCreateRuta presionado.") // <--- ESTE DEBERÍA APARECER SIEMPRE
                Log.d("HomeScreen_Nav", "Estado actual de rutas (tamaño): ${rutas.size}, Contenido: $rutas")
                Log.d("HomeScreen_Nav", "Estado actual de isRouteInitialized: $isRouteInitialized")
                if (rutas.isNotEmpty()) { // <--- AÑADIR ESTA COMPROBACIÓN
                    val ruta = rutas[0] // Ahora es seguro acceder a rutas[0]
                    if (!isRouteInitialized) {
                        navController.navigate(Screen.InitRoute.createRoute(ruta.nombreRuta))
                    } else {
                        // Opcional: Log o mensaje si la ruta ya está inicializada y se intenta crear de nuevo
                        Log.d("HomeScreen", "onCreateRuta: La ruta ya está inicializada.")
                    }
                } else {
                    // Opcional: Log o mensaje si no hay rutas disponibles
                    Log.w("HomeScreen", "onCreateRuta: No hay rutas disponibles para crear.")
                    // Podrías mostrar un Toast al usuario aquí si es apropiado
                    // Toast.makeText(context, "No hay rutas disponibles", Toast.LENGTH_SHORT).show()
                }
            },
            onVerRuta = {
                if (isRouteInitialized) { // Solo permitir ver ruta si está inicializada
                    navController.navigate(Screen.VerRuta.route)
                } else {
                    Log.d("HomeScreen", "onVerRuta: No hay ruta inicializada para ver.")
                    // Toast.makeText(context, "No hay ruta activa para ver", Toast.LENGTH_SHORT).show()
                }
            },
            onEntregar = {
                if (isRouteInitialized) { // Solo permitir entregar si hay ruta inicializada
                    navController.navigate(Screen.Delivery.route)
                } else {
                    Log.d("HomeScreen", "onEntregar: No hay ruta inicializada para entregas.")
                    // Toast.makeText(context, "No hay ruta activa para entregas", Toast.LENGTH_SHORT).show()
                }
            },
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
                totalBultos = totalEnviosEnRuta,
                entregados = entregados,
                reagendados = reagendados,
                cancelados = cancelados
            )
            messageShown = false
        } else {
            if (!messageShown) {
                Log.d("HomeScreen", "No hay rutas disponibles")
                messageShown = true
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        if (rutas.isNotEmpty() && isRouteInitialized) {
            Log.d(
                "HomeScreen_ToPieChart",
                "PASANDO A PIECHART -> totalBultos: $totalEnviosEnRuta, entregados: $entregados, reagendados: $reagendados, cancelados: $cancelados"
            )
            RoutePieChart(
                totalBultos = totalEnviosEnRuta,
                entregados = entregados,
                reagendados = reagendados,
                cancelados = cancelados,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 24.dp)
            )
        }
    }
}

