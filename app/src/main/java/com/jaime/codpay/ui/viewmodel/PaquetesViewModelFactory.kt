package com.jaime.codpay.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jaime.codpay.data.PaqueteDataStore
import com.jaime.codpay.data.PaquetesRepository

class PaquetesViewModelFactory(
    private val context: Context,
    private val paquetesRepository: PaquetesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaquetesViewModel::class.java)) {
            val paqueteDataStore = PaqueteDataStore(context)
            @Suppress("UNCHECKED_CAST")
            return PaquetesViewModel(paquetesRepository, paqueteDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}