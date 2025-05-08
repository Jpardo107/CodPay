package com.jaime.codpay.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EnviosRepositoryImpl : EnviosRepository {

    private val apiService: ApiService = RetrofitClient.instance

    override fun getEnvios(idEmpresa: Int): Flow<List<Envio>> = flow {
        Log.d("EnviosRepositoryImpl", "getEnvios() llamado con idEmpresa: $idEmpresa")
        val url = "https://www.ctinformatica.cl/envios.php?recurso=envios&idEmpresaB2B=$idEmpresa"
        Log.d("EnviosRepositoryImpl", "URL: $url")
        try {
            val response = apiService.getEnvios(idEmpresa)
            Log.d("EnviosRepositoryImpl", "response.isSuccessful: ${response.isSuccessful}")
            if (response.isSuccessful) {
                Log.d("EnviosRepositoryImpl", "Codigo de respuesta: ${response.code()}")
                val enviosResponse = response.body()
                Log.d("EnviosRepositoryImpl", "Respuesta de la API: $enviosResponse")
                if (enviosResponse != null) {
                    Log.d("EnviosRepositoryImpl", "enviosResponse.data: ${enviosResponse.data}")
                    emit(enviosResponse.data)
                } else {
                    Log.w("EnviosRepositoryImpl", "enviosResponse es null")
                    emit(emptyList())
                }
            } else {
                Log.e("EnviosRepositoryImpl", "Error en la respuesta de la API: ${response.errorBody()}")
                Log.e("EnviosRepositoryImpl", "Codigo de error: ${response.code()}")
                emit(emptyList())
            }
        }catch (e: Exception) {
            Log.e("EnviosRepositoryImpl", "Excepción al llamar a la API: ${e.message}")
            e.printStackTrace()
            emit(emptyList())
        }

    }
}