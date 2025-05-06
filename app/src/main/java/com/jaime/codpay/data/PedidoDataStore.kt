package com.jaime.codpay.data

import android.content.Context
import androidx.compose.ui.input.key.type
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PedidoDataStore(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("pedidos")
        val PEDIDOS_KEY = stringPreferencesKey("pedidos")
    }

    suspend fun savePedidos(pedidos: List<Pedido>) {
        val gson = Gson()
        val json = gson.toJson(pedidos)
        context.dataStore.edit { preferences ->
            preferences[PEDIDOS_KEY] = json
        }
    }

    fun getPedidos(): Flow<List<Pedido>> = context.dataStore.data.map { preferences ->
        val json = preferences[PEDIDOS_KEY] ?: ""
        if (json.isNotEmpty()) {
            val gson = Gson()
            val type = object : TypeToken<List<Pedido>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}