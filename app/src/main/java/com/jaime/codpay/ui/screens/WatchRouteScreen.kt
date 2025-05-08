package com.jaime.codpay.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jaime.codpay.data.Envio
import com.jaime.codpay.data.EnviosRepositoryImpl
import com.jaime.codpay.data.RutaDataStore
import com.jaime.codpay.data.UserDataStore
import com.jaime.codpay.ui.components.WatchRoute.RouteItem
import com.jaime.codpay.ui.viewmodel.EnviosViewModel
import com.jaime.codpay.ui.viewmodel.EnviosViewModelFactory
import com.jaime.codpay.utils.AbrirNavegacion
import com.jaime.codpay.utils.swap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WatchRouteScreen(navController: NavController) {
    val context = LocalContext.current
    val userDataStore = UserDataStore(context)
    val rutaDataStore = RutaDataStore(context)
    val enviosRepository = EnviosRepositoryImpl()
    val enviosViewModel: EnviosViewModel = viewModel(factory = EnviosViewModelFactory(context, enviosRepository, userDataStore, rutaDataStore))

    var envios by remember { mutableStateOf(emptyList<Envio>()) }
    val isLoading by enviosViewModel.isLoading.collectAsState()
    val error by enviosViewModel.error.collectAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        enviosViewModel.getEnvios()
    }

    val idRuta by rutaDataStore.getIdRuta().collectAsState(initial = 0)
    Log.d("WatchRouteScreen", "idRuta: $idRuta")
    val enviosFromViewModel by enviosViewModel.envios.collectAsState()
    LaunchedEffect(key1 = enviosFromViewModel) {
        envios = enviosFromViewModel.filter { it.idRuta == idRuta }
        Log.d("WatchRouteScreen", "enviosFromViewModel: ${enviosFromViewModel.size}, idRuta: $idRuta, enviosFiltrados: ${envios.size}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
        Text(
            text = "Ver Ruta",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else if (error != null) {
            LaunchedEffect(key1 = error) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        } else if (envios.isEmpty()) {
            Text(
                text = "No hay envíos disponibles",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(envios, key = { _, item -> item.idEnvio }) { index, envio ->
                    RouteItem(
                        cliente = envio.clienteFinal.nombreClienteFinal,
                        direccion = envio.clienteFinal.direccionEntrega,
                        comuna = envio.clienteFinal.comunaEntrega,
                        pedido = envio.numeroRefPedidoB2C,
                        onIrClick = {
                            val direccionCompleta = "${envio.clienteFinal.direccionEntrega}, ${envio.clienteFinal.comunaEntrega}"
                            AbrirNavegacion(context, direccionCompleta)
                        },
                        onMoveUpClick = {
                            if (index > 0) {
                                val newList = envios.toMutableList()
                                newList.swap(index, index - 1)
                                envios = newList
                                coroutineScope.launch {
                                    delay(100)
                                    listState.animateScrollToItem(index - 1)
                                }
                            }
                        },
                        onMoveDownClick = {
                            if (index < envios.size - 1) {
                                val newList = envios.toMutableList()
                                newList.swap(index, index + 1)
                                envios = newList
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