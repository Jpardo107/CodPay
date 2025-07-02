package com.jaime.codpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jaime.codpay.data.Pedido
import com.jaime.codpay.data.PedidosRepository
import com.jaime.codpay.data.UserDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PedidosViewModel(
    private val pedidosRepository: PedidosRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun getPedidos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val idEmpresa = userDataStore.getUserIdEmpresa.first() ?: 0
                val pedidosResponse = pedidosRepository.getPedidos(idEmpresa)
                val pedidosFiltrados = pedidosResponse.filter { it.estadoPedido == "Impreso" }
                _pedidos.value = pedidosFiltrados
            } catch (e: Exception) {
                _error.value = "Error al obtener los pedidos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    suspend fun actualizarRuta(
        idRuta: Int,
        agregar: List<Int>,
        quitar: List<Int>,
        estadoRuta: String
    ): Boolean {
        return pedidosRepository.actualizarRuta(idRuta, agregar, quitar, estadoRuta)
    }

}
