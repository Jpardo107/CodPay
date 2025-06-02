package com.jaime.codpay.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.jaime.codpay.data.RetrofitClient
import com.jaime.codpay.data.LoginRequest
import com.jaime.codpay.data.LoginResponse
import com.jaime.codpay.data.MfaPendingResponse
import com.jaime.codpay.data.MfaValidationRequest
import com.jaime.codpay.data.RutaDataStore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.jaime.codpay.data.UserDataStore
import com.jaime.codpay.data.RutasRepository

class LoginViewModel(
    private val context: Context,
    private val rutasRepository: RutasRepository,
    private val rutaDataStore: RutaDataStore
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loginResponse = MutableStateFlow<LoginResponse?>(null)
    val loginResponse: StateFlow<LoginResponse?> = _loginResponse

    private val apiService = RetrofitClient.instance
    private val userDataStore = UserDataStore(context)

    fun login(email: String, clave: String) {
        _isLoading.value = true
        _error.value = null

        val loginRequest = LoginRequest(email, clave)
        val call = apiService.login(loginRequest)

        call.enqueue(object : Callback<MfaPendingResponse> {
            override fun onResponse(call: Call<MfaPendingResponse>, response: Response<MfaPendingResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val res = response.body()
                    if (res?.status == "success" && res.mfa == "pending") {
                        Log.d("LoginViewModel", "Código enviado al correo: ${res.message}")
                    } else {
                        _error.value = res?.message ?: "Respuesta inesperada del servidor"
                    }
                } else {
                    _error.value = "Error en el login: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<MfaPendingResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Error de red: ${t.message}"
            }
        })
    }
    fun validarCodigo(email: String, codigo: String) {
        _isLoading.value = true
        _error.value = null
        val request = MfaValidationRequest(email, codigo)
        val call = apiService.validarMFA(request)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body()?.status == "success") {
                    val loginResponse = response.body()!!
                    _loginResponse.value = loginResponse
                    viewModelScope.launch {
                        val conductor = loginResponse.conductor
                        if (conductor != null && loginResponse.token != null) {
                            userDataStore.saveUser(
                                conductor.idConductor,
                                conductor.nombreUserB2B,
                                conductor.emailUserB2B,
                                loginResponse.token,
                                conductor.apellidosUserB2B,
                                conductor.telefonoUserB2B,
                                conductor.rutUsuarioB2B,
                                conductor.idEmpresa,
                                conductor.estadoUsuarioB2B
                            )
                        userDataStore.saveIdEmpresa(conductor.idEmpresa)
                        }
                        rutasRepository.getRutas()
                    }
                } else {
                    _error.value = response.body()?.message ?: "Error al validar el código"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Error de red: ${t.message}"
            }
        })
    }
}