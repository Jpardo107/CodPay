package com.jaime.codpay.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.jaime.codpay.data.Envio
import com.jaime.codpay.data.EnviosRepositoryImpl
import com.jaime.codpay.data.PedidosRepositoryImpl
import com.jaime.codpay.data.QrData
import com.jaime.codpay.data.RutaDataStore
import com.jaime.codpay.data.UserDataStore
import com.jaime.codpay.ui.components.QR.EscaneoPedidoDialog
import com.jaime.codpay.ui.components.QR.ModoEscaneo
import com.jaime.codpay.ui.components.WatchRoute.RouteItem
import com.jaime.codpay.ui.viewmodel.EnviosViewModel
import com.jaime.codpay.ui.viewmodel.EnviosViewModelFactory
import com.jaime.codpay.ui.viewmodel.PedidosViewModel
import com.jaime.codpay.ui.viewmodel.PedidosViewModelFactory
import com.jaime.codpay.utils.AbrirNavegacion
import com.jaime.codpay.utils.swap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WatchRouteScreen(navController: NavController) {
    var modoEscaneo by remember { mutableStateOf<ModoEscaneo?>(null) }
    val context = LocalContext.current
    val gson = Gson()
    val userDataStore = UserDataStore(context)
    val rutaDataStore = RutaDataStore(context)
    val enviosRepository = EnviosRepositoryImpl()
    val enviosViewModel: EnviosViewModel = viewModel(
        factory = EnviosViewModelFactory(
            context,
            enviosRepository,
            userDataStore,
            rutaDataStore
        )
    )

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
        Log.d(
            "WatchRouteScreen",
            "enviosFromViewModel: ${enviosFromViewModel.size}, idRuta: $idRuta, enviosFiltrados: ${envios.size}"
        )
    }

    val pedidosRepository = PedidosRepositoryImpl()
    val pedidosViewModel: PedidosViewModel = viewModel(
        factory = PedidosViewModelFactory(pedidosRepository, userDataStore)
    )

    LaunchedEffect(Unit) {
        pedidosViewModel.getPedidos()
    }
    val pedidos by pedidosViewModel.pedidos.collectAsState()

    LaunchedEffect(pedidos) {
        Log.d("WatchRouteScreen", "Pedidos libres (Creado): ${pedidos.size}")
        pedidos.forEach {
            Log.d(
                "WatchRouteScreen",
                "Pedido ${it.idPedido} → ${it.estadoPedido} → Paquetes: ${it.cantidadPaquetes}"
            )
        }
    }

    // Mapea idPedido → Set de idPaquete escaneados
    var pedidosAgregar by remember { mutableStateOf<Map<Int, Set<Int>>>(emptyMap()) }
    var pedidosQuitar by remember { mutableStateOf<Map<Int, Set<Int>>>(emptyMap()) }


// Mapea idPedido → cantidad total de paquetes que tiene (para saber cuándo completo)
    var pedidosCantidadPaquetes by remember { mutableStateOf(mutableMapOf<Int, Int>()) }

    var ultimoQrDetectado by remember { mutableStateOf<String?>(null) }
    var ultimaDeteccionTimestamp by remember { mutableStateOf(0L) }

    var ultimoPedidoEscaneadoId by remember { mutableStateOf<Int?>(null) }

    fun resetearProgreso() {
        pedidosAgregar = mutableMapOf()
        pedidosQuitar = mutableMapOf()
        ultimoPedidoEscaneadoId = null
        Log.d("WatchRouteScreen", "Progreso reseteado manualmente")
    }


    fun registrarEscaneo(
        context: Context,
        qrData: QrData,
        tipoOperacion: String
    ) {
        val pedido = pedidos.find { it.paquetes.any { paquete -> paquete.idPaquete == qrData.idPaquete } }

        if (pedido == null) {
            Log.e("WatchRouteScreen", "Pedido no encontrado para paquete ${qrData.idPaquete}")
            return
        }

        val idPedido = pedido.idPedido
        val cantidadPaquetes = pedido.cantidadPaquetes

        pedidosCantidadPaquetes[idPedido] = cantidadPaquetes

        // Si cambió de pedido, reseteamos el progreso anterior
        if (ultimoPedidoEscaneadoId != null && ultimoPedidoEscaneadoId != idPedido) {
            Log.d(
                "WatchRouteScreen",
                "Cambio de pedido detectado: antes $ultimoPedidoEscaneadoId → ahora $idPedido"
            )

            // Mostrar TOAST de cambio de pedido
            Toast.makeText(
                context,
                "Cambiando de pedido → progreso reiniciado",
                Toast.LENGTH_SHORT
            ).show()

            // Limpiar mapas
            pedidosAgregar = emptyMap()
            pedidosQuitar = emptyMap()
        }

        // Actualizamos el ultimoPedidoEscaneadoId
        ultimoPedidoEscaneadoId = idPedido

        // ÚNICO lugar donde agregamos el paquete
        when (tipoOperacion) {
            "agregar" -> {
                val paquetesActuales = pedidosAgregar[idPedido] ?: emptySet()
                pedidosAgregar = pedidosAgregar.toMutableMap().apply {
                    put(idPedido, paquetesActuales + qrData.idPaquete)
                }
                Log.d(
                    "WatchRouteScreen",
                    "Registrar AGREGAR → Pedido $idPedido → ${pedidosAgregar[idPedido]?.size}/$cantidadPaquetes paquetes escaneados"
                )
            }

            "quitar" -> {
                val paquetesActuales = pedidosQuitar[idPedido] ?: emptySet()
                pedidosQuitar = pedidosQuitar.toMutableMap().apply {
                    put(idPedido, paquetesActuales + qrData.idPaquete)
                }
                Log.d(
                    "WatchRouteScreen",
                    "Registrar QUITAR → Pedido $idPedido → ${pedidosQuitar[idPedido]?.size}/$cantidadPaquetes paquetes escaneados"
                )
            }
        }
    }

    fun enviarPutAgregarPedidos() {
        coroutineScope.launch {
            try {
                val bodyAgregar = pedidosAgregar.keys.toList()

                Log.d("WatchRouteScreen", "Enviando PUT con agregar: $bodyAgregar")

                val exito = pedidosViewModel.actualizarRuta(
                    idRuta = idRuta,
                    agregar = bodyAgregar,
                    quitar = emptyList(),
                    estadoRuta = "programado"
                )

                if (exito) {
                    Toast.makeText(context, "Ruta actualizada correctamente", Toast.LENGTH_SHORT).show()
                    resetearProgreso()
                    pedidosViewModel.getPedidos()
                } else {
                    Toast.makeText(context, "Error al actualizar ruta", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("WatchRouteScreen", "Error al enviar PUT", e)
                Toast.makeText(context, "Error al actualizar ruta", Toast.LENGTH_SHORT).show()
            }
        }
    }



    val progreso by remember(
        modoEscaneo,
        ultimoPedidoEscaneadoId,
        pedidosAgregar,
        pedidosQuitar,
        pedidosCantidadPaquetes
    ) {
        derivedStateOf {
            if (modoEscaneo != null && ultimoPedidoEscaneadoId != null) {
                val idPedido = ultimoPedidoEscaneadoId!!
                val total = pedidosCantidadPaquetes[idPedido] ?: 0

                val actuales = when (modoEscaneo!!) {
                    ModoEscaneo.AGREGAR -> pedidosAgregar[idPedido]?.size ?: 0
                    ModoEscaneo.QUITAR -> pedidosQuitar[idPedido]?.size ?: 0
                }

                "$actuales / $total paquetes escaneados"
            } else {
                null
            }
        }
    }

    val botonHabilitado = remember(
        ultimoPedidoEscaneadoId,
        pedidosAgregar,
        pedidosCantidadPaquetes
    ) {
        derivedStateOf {
            if (modoEscaneo == ModoEscaneo.AGREGAR && ultimoPedidoEscaneadoId != null) {
                val idPedido = ultimoPedidoEscaneadoId!!
                val total = pedidosCantidadPaquetes[idPedido] ?: return@derivedStateOf false
                val actuales = pedidosAgregar[idPedido]?.size ?: 0
                actuales == total && total > 0
            } else {
                false
            }
        }
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
            text = "Ver pedidos de ruta",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        Row() {
            IconButton(
                onClick = { modoEscaneo = ModoEscaneo.AGREGAR },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(16.dp))
            IconButton(
                onClick = { modoEscaneo = ModoEscaneo.QUITAR },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF44336))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Quitar",
                    tint = Color.White
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        modoEscaneo?.let { modo ->
            EscaneoPedidoDialog(
                modo = modo,
                onClose = {
                    resetearProgreso()
                    modoEscaneo = null
                },
                onQrDetected = { qrRawData ->
                    val ahora = System.currentTimeMillis()
                    if (qrRawData == ultimoQrDetectado && (ahora - ultimaDeteccionTimestamp) < 2000) {
                        Log.d("EscaneoPedidoDialog", "QR repetido ignorado")
                        return@EscaneoPedidoDialog
                    }

                    ultimoQrDetectado = qrRawData
                    ultimaDeteccionTimestamp = ahora

                    Log.d("EscaneoPedidoDialog", "QR detectado: $qrRawData")

                    try {
                        val qrData = gson.fromJson(qrRawData, QrData::class.java)
                        registrarEscaneo(
                            context = context,
                            qrData = qrData,
                            tipoOperacion = when (modo) {
                                ModoEscaneo.AGREGAR -> "agregar"
                                ModoEscaneo.QUITAR -> "quitar"
                            }
                        )

                        val mensajeToast = when (modo) {
                            ModoEscaneo.AGREGAR -> "Paquete agregado correctamente"
                            ModoEscaneo.QUITAR -> "Paquete quitado correctamente"
                        }
                        Toast.makeText(context, mensajeToast, Toast.LENGTH_SHORT).show()

                    } catch (e: Exception) {
                        Log.e("EscaneoPedidoDialog", "Error al parsear QR", e)
                        Toast.makeText(context, "Error al leer QR", Toast.LENGTH_SHORT).show()
                    }
                },
                progreso = progreso,
                onConfirm = { enviarPutAgregarPedidos() },
                confirmEnabled = botonHabilitado.value
            )
        }

        Spacer(Modifier.height(16.dp))
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
                            val direccionCompleta =
                                "${envio.clienteFinal.direccionEntrega}, ${envio.clienteFinal.comunaEntrega}"
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

@Preview(showBackground = true)
@Composable
fun WatchRouteScreenPreview() {
    val fakeEnvios = listOf(
        Envio(
            idEnvio = 1,
            idPedido = 100,
            estadoEnvio = "Pendiente",
            fechaEnvio = "2025-06-09",
            fechaEnvioReprogramado = "",
            idRuta = 1,
            numeroRefPedidoB2C = "REF-12345",
            idClienteB2C = 2,
            estadoPedido = "En Camino",
            cantidadPaquetes = 3,
            clienteFinal = com.jaime.codpay.data.ClienteFinal(
                nombreClienteFinal = "Juan Pérez",
                rutClienteFinal = "12345678-9",
                emailClienteFinal = "juan.perez@correo.cl",
                telefonoClienteFinal = "+56912345678",
                direccionEntrega = "Av. Principal 1234",
                referenciaDireccion = "Depto 101",
                regionEntrega = "Metropolitana",
                comunaEntrega = "Santiago"
            ),
            valorRecaudar = 25000.0,
            paquetes = emptyList()
        ),
        Envio(
            idEnvio = 2,
            idPedido = 101,
            estadoEnvio = "Entregado",
            fechaEnvio = "2025-06-08",
            fechaEnvioReprogramado = "",
            idRuta = 1,
            numeroRefPedidoB2C = "REF-67890",
            idClienteB2C = 3,
            estadoPedido = "Entregado",
            cantidadPaquetes = 2,
            clienteFinal = com.jaime.codpay.data.ClienteFinal(
                nombreClienteFinal = "María Gómez",
                rutClienteFinal = "98765432-1",
                emailClienteFinal = "maria.gomez@correo.cl",
                telefonoClienteFinal = "+56987654321",
                direccionEntrega = "Calle Falsa 123",
                referenciaDireccion = "Casa esquinera",
                regionEntrega = "Metropolitana",
                comunaEntrega = "Providencia"
            ),
            valorRecaudar = 18000.0,
            paquetes = emptyList()
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ver pedidos de ruta",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        Row() {
            IconButton(
                onClick = {/**/ },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(16.dp))
            IconButton(
                onClick = {/**/ },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF44336))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Quitar",
                    tint = Color.White
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(fakeEnvios, key = { _, item -> item.idEnvio }) { index, envio ->
                RouteItem(
                    cliente = envio.clienteFinal.nombreClienteFinal,
                    direccion = envio.clienteFinal.direccionEntrega,
                    comuna = envio.clienteFinal.comunaEntrega,
                    pedido = envio.numeroRefPedidoB2C,
                    onIrClick = {},
                    onMoveUpClick = {},
                    onMoveDownClick = {},
                    modifier = Modifier
                )
            }
        }
    }
}


