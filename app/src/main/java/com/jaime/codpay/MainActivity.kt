package com.jaime.codpay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jaime.codpay.data.EnviosRepository
import com.jaime.codpay.data.EnviosRepositoryImpl
import com.jaime.codpay.data.PagosRepository
import com.jaime.codpay.data.RetrofitClient
import com.jaime.codpay.ui.navigation.Screen
import com.jaime.codpay.ui.screens.DeliveryPackageScreen
import com.jaime.codpay.ui.screens.DeliveryScreen
import com.jaime.codpay.ui.screens.EntregarScreen
import com.jaime.codpay.ui.screens.HomeScreen
import com.jaime.codpay.ui.screens.InitRouteScreen
import com.jaime.codpay.ui.screens.LoginScreen
import com.jaime.codpay.ui.screens.ReagendarScreen
import com.jaime.codpay.ui.screens.RechazarScreen
import com.jaime.codpay.ui.screens.TwoFactorScreen
import com.jaime.codpay.ui.screens.WatchRouteScreen
import com.jaime.codpay.ui.theme.CodPayTheme
import com.jaime.codpay.ui.viewmodel.EntregarViewModel
import com.jaime.codpay.ui.viewmodel.EntregarViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Screen.Login.route
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(navController = navController)
                }

                composable(Screen.TwoFactor.route) {
                    TwoFactorScreen(
                        onAuthSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onBackToLogin = { navController.popBackStack("login", inclusive = false) }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(navController = navController)
                }
                composable(
                    route = Screen.InitRoute.routeWithArgs,
                    arguments = listOf(navArgument("nombreRuta") { defaultValue = "Oriente" })
                ) { backStackEntry ->
                    val nombreRuta = backStackEntry.arguments?.getString("nombreRuta") ?: "Oriente"
                    InitRouteScreen(nombreRuta = nombreRuta, navController = navController)
                }
                composable(Screen.VerRuta.route) {
                    WatchRouteScreen(navController)
                }
                composable(Screen.Delivery.route) {
                    DeliveryScreen(navController = navController)
                }
                composable(Screen.DeliveryPackage.route) {
                    DeliveryPackageScreen(navController = navController)
                }

                composable(
                    route = Screen.Entregar.routeWithArgs,
                    arguments = listOf(navArgument("envioJson") { type = NavType.StringType }) // Cambiado a envioJson
                ) { backStackEntry ->
                    val envioJson = backStackEntry.arguments?.getString("envioJson") ?: "" // Cambiado a envioJson
                    EntregarScreen(navController = navController, envioJson = envioJson) // Cambiado a envioJson
                }

                composable(
                    route = Screen.Reagendar.routeWithArgs,
                    arguments = listOf(navArgument("envioJson") { type = NavType.StringType })
                ) { backStackEntry ->
                    val envioJson = backStackEntry.arguments?.getString("envioJson") ?: ""
                    ReagendarScreen(navController = navController,envioJson = envioJson)
                }

                composable(
                    route = Screen.Rechazar.routeWithArgs,
                    arguments = listOf(navArgument("envioJson") { type = NavType.StringType })
                ) { backStackEntry ->
                    val envioJson = backStackEntry.arguments?.getString("envioJson") ?: ""

                    // Instancias de repositorios (ajústalo si tienes contenedor de dependencias)
                    val apiService = RetrofitClient.instance
                    val pagosRepository = PagosRepository(apiService)
                    val enviosRepository = EnviosRepositoryImpl()

                    val entregarViewModel: EntregarViewModel = viewModel(
                        factory = EntregarViewModelFactory(pagosRepository, enviosRepository)
                    )

                    RechazarScreen(
                        navController = navController,
                        envioJson = envioJson,
                        entregarViewModel = entregarViewModel,
                        onRechazarClick = { motivo -> /* luego lo usamos dentro de RechazarScreen */ }
                    )
                }


            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CodPayTheme {
        Greeting("Android")
    }
}