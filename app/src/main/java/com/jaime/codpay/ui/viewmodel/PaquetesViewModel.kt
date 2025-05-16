package com.jaime.codpay.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaime.codpay.data.Paquete
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
    fun clearPaquetes() {
        viewModelScope.launch {
            _paquetes.value = emptyList()
            // Si el repositorio tiene un caché, también límpialo:
            // paquetesRepository.clearCachedPaquetes()
        }
    }
}