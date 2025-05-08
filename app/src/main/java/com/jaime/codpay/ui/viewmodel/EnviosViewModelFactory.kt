package com.jaime.codpay.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jaime.codpay.data.EnviosRepository
import com.jaime.codpay.data.RutaDataStore
import com.jaime.codpay.data.UserDataStore

class EnviosViewModelFactory(
    private val context: Context,
    private val enviosRepository: EnviosRepository,
    private val userDataStore: UserDataStore,
    private val rutaDataStore: RutaDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnviosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EnviosViewModel(enviosRepository, userDataStore, rutaDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}