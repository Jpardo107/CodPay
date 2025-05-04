package com.jaime.codpay.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jaime.codpay.data.Ruta
import com.jaime.codpay.ui.components.WatchRoute.RouteItem
import com.jaime.codpay.ui.viewmodel.RutasViewModel
import com.jaime.codpay.ui.viewmodel.RutasViewModelFactory
import com.jaime.codpay.utils.AbrirNavegacion
import com.jaime.codpay.utils.swap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WatchRouteScreen(navController: NavController) {
    val context = LocalContext.current
    val rutasViewModel: RutasViewModel = viewModel(factory = RutasViewModelFactory(context))

    val rutas by rutasViewModel.rutas.collectAsState()
    val isLoading by rutasViewModel.isLoading.collectAsState()
    val error by rutasViewModel.error.collectAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp), // Padding general
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ver Ruta",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(16.dp) // Padding del título
                .align(Alignment.CenterHorizontally)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else if (error != null) {
            LaunchedEffect(key1 = error) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        } else if (rutas.isEmpty()) {
            Text(
                text = "No hay rutas disponibles",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Para que ocupe el espacio disponible
                contentPadding = PaddingValues(16.dp), // Padding de la lista
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre items
            ) {
                itemsIndexed(rutas, key = { _, item -> item.idEnvio }) { index, ruta ->
                    RouteItem(
                        cliente = ruta.nombreClienteFinal,
                        direccion = ruta.direccionEntrega,
                        comuna = ruta.comunaEntrega,
                        pedido = ruta.numeroRefPedidoB2C,
                        onIrClick = {
                            val direccionCompleta = "${ruta.direccionEntrega}, ${ruta.comunaEntrega}"
                            AbrirNavegacion(context, direccionCompleta)
                        },
                        onMoveUpClick = {
                            if (index > 0) {
                                val newList = rutas.toMutableList()
                                newList.swap(index, index - 1)
                                rutasViewModel.updateRutas(newList)
                                coroutineScope.launch {
                                    delay(100)
                                    listState.animateScrollToItem(index - 1)
                                }
                            }
                        },
                        onMoveDownClick = {
                            if (index < rutas.size - 1) {
                                val newList = rutas.toMutableList()
                                newList.swap(index, index + 1)
                                rutasViewModel.updateRutas(newList)
                                coroutineScope.launch {
                                    delay(100)
                                    listState.animateScrollToItem(index + 1)
                                }
                            }
                        },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}
