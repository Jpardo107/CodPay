package com.jaime.codpay.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jaime.codpay.data.PaqueteDataStore
import com.jaime.codpay.data.PaquetesRepository
import com.jaime.codpay.data.RutaDataStore
import com.jaime.codpay.data.RutasRepository
import com.jaime.codpay.data.UserDataStore
import com.jaime.codpay.data.RetrofitClient

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val userDataStore = UserDataStore(context)
            val rutaDataStore = RutaDataStore(context)
            val paquetesRepository = PaquetesRepository(RetrofitClient.instance, PaqueteDataStore(context))
            val rutasRepository = RutasRepository(RetrofitClient.instance, userDataStore, rutaDataStore, paquetesRepository)
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(context, rutasRepository, rutaDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}