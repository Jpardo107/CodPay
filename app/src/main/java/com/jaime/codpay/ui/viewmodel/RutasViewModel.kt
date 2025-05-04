package com.jaime.codpay.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaime.codpay.data.Ruta
import com.jaime.codpay.data.RutasResponse
import com.jaime.codpay.data.RetrofitClient
import com.jaime.codpay.data.UserDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.Response

class RutasViewModel(private val context: Context) : ViewModel() {

    private val _rutas = MutableStateFlow<List<Ruta>>(emptyList())
    val rutas: StateFlow<List<Ruta>> = _rutas

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val apiService = RetrofitClient.instance
    private val userDataStore = UserDataStore(context)

    init {
        cargarRutas()
    }

    private fun cargarRutas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Recolectar el valor del Flow de forma segura
                //val idConductor = userDataStore.getUserId.firstOrNull()
                val idConductor = 44
                Log.d("RutasViewModel", "ID del conductor obtenido: $idConductor")
                if (idConductor != null) {
                    val response: Response<RutasResponse> = apiService.getRutas()
                    if (response.isSuccessful) {
                        val rutasResponse = response.body()
                        if (rutasResponse != null && rutasResponse.status == "success") {
                            val rutasFiltradas = rutasResponse.data.filter { it.idConductor == idConductor }
                            _rutas.value = rutasFiltradas
                            Log.d("RutasViewModel", "Rutas cargadas: ${rutasFiltradas.size}")
                        } else {
                            _error.value = "Error al obtener las rutas: ${rutasResponse?.status}"
                        }
                    } else {
                        _error.value = "Error al obtener las rutas: ${response.code()}"
                    }
                } else {
                    _error.value = "Error al obtener el ID del conductor"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateRutas(newRutas: List<Ruta>) {
        _rutas.value = newRutas
    }
}