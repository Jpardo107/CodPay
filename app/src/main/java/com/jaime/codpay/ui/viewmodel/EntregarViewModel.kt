// En EntregarViewModel.kt (o el ViewModel de tu EntregarScreen)
package com.jaime.codpay.ui.viewmodel // o tu paquete de viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jaime.codpay.data.PagosRepository // Importa tu repositorio
import com.jaime.codpay.data.PagoRequest
import com.jaime.codpay.data.PagoResponse
import com.jaime.codpay.data.Envio // Asumo que tienes una clase Envio con idEnvio y valorRecaudar
import com.jaime.codpay.data.EnviosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

// Define un tipo para el resultado de la operación de pago, para observarlo desde la UI
sealed class ResultadoPago {
    object Loading : ResultadoPago()
    data class Success(val pagoResponse: PagoResponse) : ResultadoPago()
    data class Error(val mensaje: String, val codigoError: Int? = null) : ResultadoPago()
    object Idle : ResultadoPago() // Estado inicial o después de completar
}

class EntregarViewModel(
    private val pagosRepository: PagosRepository,
    private val enviosRepository: EnviosRepository
    // Podrías necesitar otros repositorios o fuentes de datos, como para obtener idEmpresaB2B
    // private val userRepository: UserRepository // Ejemplo si idEmpresaB2B viene de datos de usuario
) : ViewModel() {

    private val _resultadoPago = MutableStateFlow<ResultadoPago>(ResultadoPago.Idle)
    val resultadoPago: StateFlow<ResultadoPago> = _resultadoPago

    // Asumo que el ViewModel tiene acceso al envío actual y al idEmpresaB2B
    // Esto podría venir de un StateFlow, parámetros pasados, o cargado de otro repo.
    // Por simplicidad, lo pongo como propiedades que se deben establecer.
    // En una app real, esto sería más robusto (ej. cargado al iniciar el ViewModel).
    var envioActual: Envio? = null // Debes asegurarte de que esto se establezca
    var idEmpresaB2B: Int? = null   // y esto también

    fun procesarPagoEfectivo() {
        val currentEnvio = envioActual
        val currentIdEmpresa = idEmpresaB2B

        if (currentEnvio == null || currentIdEmpresa == null) {
            _resultadoPago.value = ResultadoPago.Error("Datos del envío o empresa no disponibles.")
            Log.e("EntregarViewModel", "Error: envioActual o idEmpresaB2B es nulo.")
            return
        }

        // 1. Construir el objeto PagoRequest
        val pagoDetails = PagoRequest(
            idEnvio = currentEnvio.idEnvio, // Asumiendo que Envio tiene idEnvio
            idEmpresaB2B = currentIdEmpresa,
            metodoPago = "Efectivo",
            referenciaTransaccion = null,
            observacionesPago = "Pago entregado en efectivo al momento de la entrega",
            montoPagado = currentEnvio.valorRecaudar.toInt() // Asumiendo que Envio tiene valorRecaudar y es compatible con Int
        )

        viewModelScope.launch {
            _resultadoPago.value = ResultadoPago.Loading
            try {
                Log.d("EntregarViewModel", "Enviando solicitud de pago: $pagoDetails")
                val response: Response<PagoResponse> = pagosRepository.registrarPagoEnServidor(pagoDetails)

                if (response.isSuccessful) {
                    val pagoResponse = response.body()
                    if (pagoResponse != null) {
                        Log.i("EntregarViewModel", "Pago registrado con éxito: $pagoResponse")
                        _resultadoPago.value = ResultadoPago.Success(pagoResponse)
                        // Aquí podrías realizar acciones adicionales, como actualizar el estado del envío localmente,
                        // navegar a otra pantalla, etc.
                    } else {
                        Log.e("EntregarViewModel", "Respuesta exitosa pero cuerpo vacío.")
                        _resultadoPago.value = ResultadoPago.Error("Respuesta exitosa pero cuerpo vacío.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("EntregarViewModel", "Error al registrar pago. Código: ${response.code()}. Mensaje: $errorBody")
                    _resultadoPago.value = ResultadoPago.Error("Error ${response.code()}: $errorBody", response.code())
                }
            } catch (e: Exception) {
                Log.e("EntregarViewModel", "Excepción al registrar pago: ${e.message}", e)
                _resultadoPago.value = ResultadoPago.Error("Excepción: ${e.message ?: "Error de conexión"}")
            }
        }
    }

    fun resetResultadoPago() {
        _resultadoPago.value = ResultadoPago.Idle
    }
    fun actualizarEstadoEnvio(idEnvio: Int, nuevoEstado: String, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exito = enviosRepository.actualizarEstadoEnvio(idEnvio, nuevoEstado)
            onResultado(exito)
        }
    }
}


class EntregarViewModelFactory(
    private val pagosRepository: PagosRepository,
    private val enviosRepository: EnviosRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EntregarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EntregarViewModel(pagosRepository, enviosRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}