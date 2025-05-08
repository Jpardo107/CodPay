package com.jaime.codpay.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.jaime.codpay.data.RetrofitClient
import com.jaime.codpay.data.LoginResponse
import com.jaime.codpay.data.LoginRequest
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

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _loginResponse.value = response.body()
                    val loginResponse = response.body()!!
                    viewModelScope.launch {
                        userDataStore.saveUser(
                            loginResponse.conductor.idConductor,
                            loginResponse.conductor.nombreUserB2B,
                            loginResponse.conductor.emailUserB2B,
                            loginResponse.token,
                            loginResponse.conductor.apellidosUserB2B,
                            loginResponse.conductor.telefonoUserB2B,
                            loginResponse.conductor.rutUsuarioB2B,
                            loginResponse.conductor.idEmpresa,
                            loginResponse.conductor.estadoUsuarioB2B
                        )
                        Log.d("LoginViewModel", "Datos guardados: ${loginResponse.conductor}")
                        // Llamar a getRutas() después de un login exitoso
                        rutasRepository.getRutas()
                        userDataStore.saveIdEmpresa(loginResponse.conductor.idEmpresa)
                    }
                } else {
                    _error.value = "Error en el login: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Error de red: ${t.message}"
            }
        })
    }
}