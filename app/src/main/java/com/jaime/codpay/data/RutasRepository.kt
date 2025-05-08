package com.jaime.codpay.data

import android.util.Log
import kotlinx.coroutines.flow.first
import java.lang.Exception

class RutasRepository(
    private val apiService: ApiService,
    private val userDataStore: UserDataStore,
    private val rutaDataStore: RutaDataStore,
    private val paquetesRepository: PaquetesRepository
) {
    suspend fun getRutas(): List<Ruta> {
        val idEmpresa = userDataStore.getUserIdEmpresa.first() ?: 0
        val idConductor = userDataStore.getUserId.first() ?: 0

        Log.d("RutasRepository", "idEmpresa: $idEmpresa, idConductor: $idConductor")
        val url = "https://www.ctinformatica.cl/envios.php?recurso=rutas&idEmpresaB2B=$idEmpresa&idConductor=$idConductor"
        Log.d("RutasRepository", "URL: $url")

        try {
            val response = apiService.getRutas(idEmpresa, idConductor)
            Log.d("RutasRepository", "response.isSuccessful: ${response.isSuccessful}")
            if (response.isSuccessful) {
                Log.d("RutasRepository", "Codigo de respuesta: ${response.code()}")
                val rutasResponse = response.body()
                Log.d("RutasRepository", "Respuesta de la API: $rutasResponse")
                if (rutasResponse != null) {
                    Log.d("RutasRepository", "rutasResponse.data: ${rutasResponse.data}")
                    rutaDataStore.saveRutas(rutasResponse.data)
                    if (rutasResponse.data.isNotEmpty()) {
                        rutaDataStore.saveIdRuta(rutasResponse.data[0].idRuta)
                    }
                    rutasResponse.data.forEach { ruta ->
                        Log.d("RutasRepository", "Llamando a getPaquetesPorRuta() para la ruta: ${ruta.idRuta}")
                        val paquetesResponse = paquetesRepository.getPaquetesPorRuta(ruta.idRuta)
                        if (paquetesResponse.isNotEmpty()) {
                            // Corrección: Usar paqueteDataStore.savePaquetes()
                            paquetesRepository.paqueteDataStore.savePaquetes(paquetesResponse)
                        }
                    }
                    return rutasResponse.data
                } else {
                    Log.w("RutasRepository", "rutasResponse es null")
                    return emptyList()
                }
            } else {
                Log.e("RutasRepository", "Error en la respuesta de la API: ${response.errorBody()}")
                Log.e("RutasRepository", "Codigo de error: ${response.code()}")
                return emptyList()
            }
        } catch (e: Exception) {
            Log.e("RutasRepository", "Excepción al llamar a la API: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }

    }
}