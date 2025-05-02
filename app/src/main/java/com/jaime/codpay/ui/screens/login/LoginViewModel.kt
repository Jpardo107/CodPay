package com.jaime.codpay.ui.screens.login
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.jaime.codepay.data.repository.AuthRepository
//import com.jaime.codepay.domain.models.LoginRequest
//import com.jaime.codepay.domain.models.Conductor
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class LoginViewModel @Inject constructor(
//    private val authRepository: AuthRepository
//) : ViewModel() {
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    private val _conductor = MutableStateFlow<Conductor?>(null)
//    val conductor: StateFlow<Conductor?> = _conductor
//
//    private val _error = MutableStateFlow<String?>(null)
//    val error: StateFlow<String?> = _error
//
//    fun login(email: String, password: String) {
//        _isLoading.value = true
//        viewModelScope.launch {
//            try {
//                val request = LoginRequest(email, password)
//                val response = authRepository.login(request)
//                _conductor.value = response.conductor
//                _error.value = null
//            } catch (e: Exception) {
//                _error.value = "Error al iniciar sesión: ${e.message}"
//                _conductor.value = null
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//}
