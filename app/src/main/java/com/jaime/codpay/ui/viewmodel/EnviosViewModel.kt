package com.jaime.codpay.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaime.codpay.data.Envio
import com.jaime.codpay.data.EnviosRepository
import com.jaime.codpay.data.RutaDataStore
import com.jaime.codpay.data.UserDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EnviosViewModel(
    private val enviosRepository: EnviosRepository,
    private val userDataStore: UserDataStore,
    private val rutaDataStore: RutaDataStore
) : ViewModel() {

    private val _envios = MutableStateFlow<List<Envio>>(emptyList())
    val envios: StateFlow<List<Envio>> = _envios.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun getEnvios() {
        viewModelScope.launch {
            _isLoading.update { true }
            _error.update { null }
            val idEmpresa = userDataStore.getUserIdEmpresa.first()
            val idRuta = rutaDataStore.getIdRuta().first()
            Log.d("EnviosViewModel", "getEnvios: idEmpresa = $idEmpresa, idRuta = $idRuta")
            enviosRepository.getEnvios(idEmpresa ?: 0).collect { envios ->
                Log.d("EnviosViewModel", "Envíos sin filtrar: $envios")
                val enviosFiltrados = envios.filter { it.idRuta == idRuta }
                Log.d("EnviosViewModel", "Envíos filtrados: $enviosFiltrados")
                _envios.update { enviosFiltrados }
                _isLoading.update { false }
            }
        }
    }
    fun getEnvioByIdEnvio(idEnvio: String): Envio? {
        Log.d("EnviosViewModel", "getEnvios: idEnvio = $idEnvio")
        Log.d("EnviosViewModel", "Lista de envíos: ${_envios.value}")
        return _envios.value.find { it.numeroRefPedidoB2C == idEnvio }
    }
    fun actualizarEstadoDeEnvios(envios: List<Envio>, nuevoEstado: String, onFinalizado: (Boolean) -> Unit) {
        viewModelScope.launch {
            var todosExitosos = true

            for (envio in envios) {
                Log.e("EnviosViewModel", "idEnvio: ${envio.idEnvio} -- nuevoEstado: $nuevoEstado")
                val exito = enviosRepository.actualizarEstadoEnvio(envio.idEnvio, nuevoEstado)
                if (!exito) {
                    todosExitosos = false
                    Log.e("EnviosViewModel", "Error al actualizar estado de envio ${envio.idEnvio}")
                }
            }

            onFinalizado(todosExitosos)
        }
    }
    fun reagendarEnvio(envio: Envio, nuevaFecha: String, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            val resultado = enviosRepository.reagendarEnvio(envio.idEnvio, nuevaFecha)
            onResultado(resultado)
        }
    }



}