package com.jaime.codpay.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jaime.codpay.data.ApiService
import com.jaime.codpay.data.PaqueteDataStore
import com.jaime.codpay.data.PaquetesRepository
import com.jaime.codpay.data.Ruta
import com.jaime.codpay.data.RutasRepository
import com.jaime.codpay.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.jaime.codpay.data.RutaDataStore
import com.jaime.codpay.data.UserDataStore
import kotlinx.coroutines.flow.asStateFlow

class RutasViewModel(
    private val repository: RutasRepository,
    private val paquetesRepo: PaquetesRepository
) : ViewModel() {
    private val _rutas = MutableStateFlow<List<Ruta>>(emptyList())
    val rutas: StateFlow<List<Ruta>> = _rutas
    val paquetesRepository: PaquetesRepository = paquetesRepo


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isRouteCurrentlyInitialized = MutableStateFlow(false)
    val isRouteCurrentlyInitialized: StateFlow<Boolean> = _isRouteCurrentlyInitialized.asStateFlow()

    init {
        Log.d(
            "RutasViewModel_Lifecycle",
            "RutasViewModel Instancia Creada. HashCode: ${this.hashCode()}"
        )
        getRutas()
    }

    fun getRutas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val rutas = repository.getRutas()
                _rutas.value = rutas
            } catch (e: Exception) {
                _error.value = "Error al obtener las rutas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearRutas() {
        viewModelScope.launch {
            _rutas.value = emptyList()
            _isRouteCurrentlyInitialized.value = false
        }
    }

    fun markRouteAsInitialized() {
        _isRouteCurrentlyInitialized.value = true
        Log.d(
            "RutasViewModel_Lifecycle",
            "markRouteAsInitialized: _isRouteCurrentlyInitialized.value ahora es TRUE. HashCode: ${this.hashCode()}"
        )
    }

    fun markRouteAsNotInitialized() {
        _isRouteCurrentlyInitialized.value = false
        Log.d(
            "RutasViewModel_Lifecycle",
            "markRouteAsNotInitialized: _isRouteCurrentlyInitialized.value ahora es FALSE. HashCode: ${this.hashCode()}"
        )
    }
}

class RutasViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    lateinit var paquetesRepository: PaquetesRepository
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RutasViewModel::class.java)) {
            val userDataStore = UserDataStore(context)
            val rutaDataStore = RutaDataStore(context)
            val paqueteDataStore = PaqueteDataStore(context)
            paquetesRepository = PaquetesRepository(RetrofitClient.instance, paqueteDataStore)
            val rutasRepository = RutasRepository(
                RetrofitClient.instance,
                userDataStore,
                rutaDataStore,
                paquetesRepository
            )
            @Suppress("UNCHECKED_CAST")
            return RutasViewModel(rutasRepository, paquetesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}