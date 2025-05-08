package com.jaime.codpay.data

import android.content.Context
import android.util.Log
import androidx.compose.ui.input.key.type
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RutaDataStore(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("rutas")
        val RUTAS_KEY = stringPreferencesKey("rutas")
        val ID_RUTA = intPreferencesKey("id_ruta")
    }

    suspend fun saveRutas(rutas: List<Ruta>) {
        Log.d("RutaDataStore", "Guardando rutas: $rutas")
        val gson = Gson()
        val json = gson.toJson(rutas)
        Log.d("RutaDataStore", "Guardando rutas: $json")
        context.dataStore.edit { preferences ->
            preferences[RUTAS_KEY] = json
        }
    }

    fun getRutas(): Flow<List<Ruta>> = context.dataStore.data.map { preferences ->
        val json = preferences[RUTAS_KEY] ?: ""
        Log.d("RutaDataStore", "Leyendo rutas: $json")
        if (json.isNotEmpty()) {
            val gson = Gson()
            val type = object : TypeToken<List<Ruta>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    suspend fun saveIdRuta(idRuta: Int) {
        Log.d("RutaDataStore", "saveIdRuta: $idRuta")
        context.dataStore.edit { preferences ->
            preferences[ID_RUTA] = idRuta
        }
    }
    fun getIdRuta(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            val idRuta = preferences[ID_RUTA] ?: 0
            Log.d("RutaDataStore", "getIdRuta: $idRuta")
            idRuta
        }
    }
}