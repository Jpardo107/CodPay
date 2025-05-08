package com.jaime.codpay.data

import android.util.Log
import kotlinx.coroutines.flow.first
import java.lang.Exception

class PaquetesRepository(
    private val apiService: ApiService,
    val paqueteDataStore: PaqueteDataStore
) {
    suspend fun getPaquetesPorRuta(idRuta: Int): List<Paquete> {
        Log.d("PaquetesRepository", "getPaquetesPorRuta() llamado con idRuta: $idRuta")
        val url = "https://www.ctinformatica.cl/envios.php?recurso=paquetesPorRuta&idRuta=$idRuta"
        Log.d("PaquetesRepository", "URL: $url")

        try {
            val response = apiService.getPaquetesPorRuta(idRuta)
            Log.d("PaquetesRepository", "response.isSuccessful: ${response.isSuccessful}")
            if (response.isSuccessful) {
                Log.d("PaquetesRepository", "Codigo de respuesta: ${response.code()}")
                val paquetesResponse = response.body()
                Log.d("PaquetesRepository", "Respuesta de la API: $paquetesResponse")
                if (paquetesResponse != null) {
                    Log.d("PaquetesRepository", "paquetesResponse.paquetes: ${paquetesResponse.paquetes}")
                    paqueteDataStore.savePaquetes(paquetesResponse.paquetes)
                    Log.d("PaquetesRepository", "Paquetes guardados en DataStore")
                    return paquetesResponse.paquetes
                } else {
                    Log.w("PaquetesRepository", "paquetesResponse es null")
                    return emptyList()
                }
            } else {
                Log.e("PaquetesRepository", "Error en la respuesta de la API: ${response.errorBody()}")
                Log.e("PaquetesRepository", "Codigo de error: ${response.code()}")
                return emptyList()
            }
        } catch (e: Exception) {
            Log.e("PaquetesRepository", "Excepción al llamar a la API: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }
}