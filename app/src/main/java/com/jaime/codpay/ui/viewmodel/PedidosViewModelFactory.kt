package com.jaime.codpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jaime.codpay.data.PedidosRepository
import com.jaime.codpay.data.UserDataStore

class PedidosViewModelFactory(
    private val pedidosRepository: PedidosRepository,
    private val userDataStore: UserDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PedidosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PedidosViewModel(pedidosRepository, userDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
