package com.jaime.codpay.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaime.codpay.data.Paquete
import com.jaime.codpay.data.PaqueteConEnvio
import com.jaime.codpay.data.PaqueteDataStore
import com.jaime.codpay.data.PaquetesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaquetesViewModel(
    private val paquetesRepository: PaquetesRepository,
    private val paqueteDataStore: PaqueteDataStore
) : ViewModel() {
    private val _paquetes = MutableStateFlow<List<Paquete>>(emptyList())
    val paquetes: StateFlow<List<Paquete>> = _paquetes
    private val _paquetesConEnvio = MutableStateFlow<List<PaqueteConEnvio>>(emptyList())
    val paquetesConEnvio: StateFlow<List<PaqueteConEnvio>> = _paquetesConEnvio


    init {
        getPaquetes()
    }

    private fun getPaquetes() {
        viewModelScope.launch {
            paqueteDataStore.getPaquetes().collect { paquetes ->
                Log.d("PaquetesViewModel", "Paquetes leidos de PaqueteDataStore: $paquetes")
                _paquetes.value = paquetes
            }
        }
    }
    fun eliminarPaquete(idPaquete: Int) {
        var paqueteEncontrado = false
        _paquetes.update { paquetesActuales ->
            val paqueteAEliminar = paquetesActuales.find { it.idPaquete == idPaquete }
            if (paqueteAEliminar != null) {
                paqueteEncontrado = true
            }
            paquetesActuales.filter { it.idPaquete != idPaquete }
        }
        if (paqueteEncontrado) {
            Log.d("PaquetesViewModel_Debug", "Paquete $idPaquete eliminado. Nuevo tamaño de _paquetes: ${_paquetes.value.size}")
        } else {
            Log.d("PaquetesViewModel_Debug", "Paquete $idPaquete NO encontrado en la lista para eliminar. Tamaño actual: ${_paquetes.value.size}")
        }
    }
    fun eliminarPaqueteConEnvio(idPaquete: Int) {
        _paquetesConEnvio.update { paquetesActuales ->
            paquetesActuales.filter { it.idPaquete != idPaquete }
        }
        Log.d("PaquetesViewModel_Debug", "PaqueteConEnvio $idPaquete eliminado. Nuevo tamaño: ${_paquetesConEnvio.value.size}")
    }
    fun clearPaquetes() {
        viewModelScope.launch {
            _paquetes.value = emptyList()
            // Si el repositorio tiene un caché, también límpialo:
            // paquetesRepository.clearCachedPaquetes()
        }
    }
    fun setEnvios(envios: List<com.jaime.codpay.data.Envio>) {
        _paquetesConEnvio.value = envios.flatMap { envio ->
            envio.paquetes.map { paquete ->
                PaqueteConEnvio(
                    idPaquete = paquete.idPaquete,
                    codigoPaquete = paquete.codigoPaquete,
                    descripcionPaquete = paquete.descripcionPaquete,
                    codigoQr = paquete.codigoQr,
                    idEnvio = envio.idEnvio,
                    numeroRefPedidoB2C = envio.numeroRefPedidoB2C,
                    idClienteB2C = envio.idClienteB2C,
                    clienteNombre = envio.clienteFinal.nombreClienteFinal,
                    direccionEntrega = envio.clienteFinal.direccionEntrega,
                    comunaEntrega = envio.clienteFinal.comunaEntrega,
                    nombreClienteB2C = envio.nombreClienteB2C
                )
            }
        }
        Log.d("PaquetesViewModel", "paquetesConEnvio actualizado: ${_paquetesConEnvio.value.size} elementos.")
    }
}