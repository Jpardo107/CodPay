package com.jaime.codpay.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EnviosRepositoryImpl : EnviosRepository {

    private val apiService: ApiService = RetrofitClient.instance

     override suspend fun actualizarEstadoEnvio(idEnvio: Int, estado: String): Boolean {
        return try {
            val request = EnvioActualizacionRequest(idEnvio, estado)
            Log.d(
                "EnviosRepositoryImpl",
                "Enviando request: $request"
            )
            val response = apiService.actualizarEstadoEnvio(request)
            Log.d(
                "EnviosRepositoryImpl",
                "update idEnvio=$idEnvio estado=$estado code=${response.code()} error=${response.errorBody()?.string()}"
            )
            Log.d("EnviosRepositoryImpl", "update idEnvio=$idEnvio estado=$estado response.code=${response.code()} error=${response.errorBody()?.string()}")
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

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

    override suspend fun reagendarEnvio(idEnvio: Int, nuevaFecha: String): Boolean {
        return try {
            val request = EnvioReagendamientoRequest(
                idEnvio = idEnvio,
                estadoEnvio = "Reprogramado",
                fechaReprogramada = nuevaFecha
            )
            val response = apiService.reagendarEnvio(request)
            Log.d("EnviosRepositoryImpl", "Reagendar: $request → ${response.code()}")
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("EnviosRepositoryImpl", "Error reagendando envio", e)
            false
        }
    }

}