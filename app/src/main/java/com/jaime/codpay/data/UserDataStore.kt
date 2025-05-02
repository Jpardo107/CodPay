package com.jaime.codpay.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataStore(private val context: Context) {

    // to make sure there is only one instance
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_data")
        val USER_ID_KEY = intPreferencesKey("user_id")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        val USER_APELLIDO_KEY = stringPreferencesKey("user_apellido")
        val USER_TELEFONO_KEY = stringPreferencesKey("user_telefono")
        val USER_RUT_KEY = stringPreferencesKey("user_rut")
        val USER_IDEMPRESA_KEY = intPreferencesKey("user_idempresa")
        val USER_ESTADO_KEY = intPreferencesKey("user_estado")
    }

    //get the saved email
    val getUserId: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID_KEY] ?: 0
        }

    val getUserName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_NAME_KEY] ?: ""
        }
    val getUserApellido: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_APELLIDO_KEY] ?: ""
        }
    val getUserEmail: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_EMAIL_KEY] ?: ""
        }
    val getUserTelefono: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_TELEFONO_KEY] ?: ""
        }
    val getUserRut: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_RUT_KEY] ?: ""
        }
    val getUserIdEmpresa: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_IDEMPRESA_KEY] ?: 0
        }
    val getUserEstado: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ESTADO_KEY] ?: 0
        }
    val getUserToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_TOKEN_KEY] ?: ""
        }

    //save email into datastore
    suspend fun saveUser(id: Int, name: String, email: String, token: String, apellido: String, telefono: String, rut: String, idEmpresa: Int, estado: Int) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = id
            preferences[USER_NAME_KEY] = name
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_TOKEN_KEY] = token
            preferences[USER_APELLIDO_KEY] = apellido
            preferences[USER_TELEFONO_KEY] = telefono
            preferences[USER_RUT_KEY] = rut
            preferences[USER_IDEMPRESA_KEY] = idEmpresa
            preferences[USER_ESTADO_KEY] = estado
        }
    }
}