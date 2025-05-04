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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.jaime.codpay.data.Pedido
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
                    arguments = listOf(navArgument("pedidoJson") { type = NavType.StringType })
                ) { backStackEntry ->
                    val pedidoJson = backStackEntry.arguments?.getString("pedidoJson") ?: ""
                    EntregarScreen(navController = navController, pedidoJson = pedidoJson)
                }

                composable(
                    route = Screen.Reagendar.routeWithArgs,
                    arguments = listOf(navArgument("pedidoJson") { type = NavType.StringType })
                ) { backStackEntry ->
                    val pedidoJson = backStackEntry.arguments?.getString("pedidoJson") ?: ""
                    ReagendarScreen(navController = navController, onReagendarClick = { motivo, fecha -> /* acción */ },pedidoJson = pedidoJson)
                }

                composable(
                    route = Screen.Rechazar.routeWithArgs,
                    arguments = listOf(navArgument("pedidoJson") { type = NavType.StringType })
                ) { backStackEntry ->
                    val pedidoJson = backStackEntry.arguments?.getString("pedidoJson") ?: ""
                    RechazarScreen(navController = navController, onRechazarClick = { motivo -> /* acción */ },pedidoJson = pedidoJson)
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