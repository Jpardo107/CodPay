package com.jaime.codpay.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object TwoFactor : Screen("two_factor")
    object Home : Screen("home")
    object InitRoute : Screen("init_route/{nombreRuta}") {
        fun createRoute(nombreRuta: String) = "init_route/$nombreRuta"
        val routeWithArgs = "init_route/{nombreRuta}"
    }
    object VerRuta : Screen("ver_ruta")
    object Delivery: Screen("delivery")
    object Entregar: Screen("entregar")
    object DeliveryPackage: Screen("delivery_package_screen")
}