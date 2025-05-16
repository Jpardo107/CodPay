package com.jaime.codpay.data

import android.content.Context
import android.util.Log
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

class PaqueteDataStore(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("paquetes")
        val PAQUETES_KEY = stringPreferencesKey("paquetes")
    }

    suspend fun savePaquetes(paquetes: List<Paquete>) {
        Log.d("PaqueteDataStore", "savePaquetes() llamado")
        val gson = Gson()
        val json = gson.toJson(paquetes)
        Log.d("PaqueteDataStore", "Guardando paquetes: $json")
        context.dataStore.edit { preferences ->
            preferences[PAQUETES_KEY] = json
        }
    }

    fun getPaquetes(): Flow<List<Paquete>> = context.dataStore.data.map { preferences ->
        Log.d("PaqueteDataStore", "getPaquetes() llamado")
        val json = preferences[PAQUETES_KEY] ?: ""
        Log.d("PaqueteDataStore", "Leyendo paquetes: $json")
        if (json.isNotEmpty()) {
            val gson = Gson()
            val type = object : TypeToken<List<Paquete>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    suspend fun clearPaquetes() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}