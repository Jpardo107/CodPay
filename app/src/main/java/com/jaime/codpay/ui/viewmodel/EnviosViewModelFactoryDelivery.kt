package com.jaime.codpay.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jaime.codpay.data.EnviosRepository
import com.jaime.codpay.data.RutaDataStore
import com.jaime.codpay.data.UserDataStore
import com.jaime.codpay.data.RetrofitClient
import com.jaime.codpay.data.EnviosRepositoryImpl

class EnviosViewModelFactoryDelivery(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnviosViewModel::class.java)) {
            val userDataStore = UserDataStore(context)
            val rutaDataStore = RutaDataStore(context)
            val enviosRepository: EnviosRepository = EnviosRepositoryImpl()
            @Suppress("UNCHECKED_CAST")
            return EnviosViewModel(enviosRepository, userDataStore, rutaDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}