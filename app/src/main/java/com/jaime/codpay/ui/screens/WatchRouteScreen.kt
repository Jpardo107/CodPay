package com.jaime.codpay.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jaime.codpay.Model.RutaEntrega
import com.jaime.codpay.ui.components.WatchRoute.RouteItem
import com.jaime.codpay.utils.AbrirNavegacion
import com.jaime.codpay.utils.swap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WatchRouteScreen(navController: NavController) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val listaRuta = remember {
        mutableStateListOf(
            RutaEntrega(1,"Cliente 10", "Apoquindo 5550", "Las Condes", 123456),
            RutaEntrega(2,"Cliente 11", "Emiliano Figueroa 15019", "Viña del Mar",654321),
            RutaEntrega(3,"Cliente 20", "Av. Pajaritos 2345", "Maipú", 926374),
            RutaEntrega(4,"cliente 1", "pasaje marilita 380", "Valdivia", 382938),
            RutaEntrega(5,"Cliente 2", "dos oriente 429", "Valdivia", 173926),
            RutaEntrega(6,"Cliente 3", "Volcan licancabur 397", "Pudahuel", 320376),
            RutaEntrega(7,"Cliente 4", "Rio Baker 5707", "Quinta Normal", 463728),
            RutaEntrega(8,"Cliente 5", "Ignacio de la carrera 966, ", "Valdivia", 266553),
            RutaEntrega(9,"Cliente 6", "Arauco 365", "Valdivia", 333726),
            RutaEntrega(10,"Cliente 7", "Arauco 365", "Valdivia", 333829),
            RutaEntrega(11,"Cliente 18", "Av. Pajaritos 2345", "Maipú", 332412)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
    ) {
        Text(
            text = "Ver Ruta",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        LazyColumn (
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            itemsIndexed(listaRuta, key = { _, item -> item.id }) { index, item ->
                RouteItem(
                    cliente = item.cliente,
                    direccion = item.direccion,
                    comuna = item.comuna,
                    pedido = item.pedido,
                    onIrClick = {
                        val direccionCompleta = "${item.direccion}, ${item.comuna}"
                        AbrirNavegacion(context, direccionCompleta)
                    },
                    onMoveUpClick = {
                        if (index > 0) {
                            listaRuta.swap(index, index - 1)
                            coroutineScope.launch {
                                delay(100)
                                listState.animateScrollToItem(index - 1)
                            }
                        }
                    },
                    onMoveDownClick = {
                        if (index < listaRuta.size - 1) {
                            listaRuta.swap(index, index + 1)
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