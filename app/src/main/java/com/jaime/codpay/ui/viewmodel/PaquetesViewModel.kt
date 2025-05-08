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
        _paquetes.update { paquetes ->
            paquetes.filter { it.idPaquete != idPaquete }
        }
    }
}