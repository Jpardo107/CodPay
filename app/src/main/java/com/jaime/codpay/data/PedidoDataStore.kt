//package com.jaime.codpay.data
//
//import kotlinx.coroutines.flow.first
//
//class RutasRepository(
//    private val apiService: ApiService,
//    private val userDataStore: UserDataStore,
//    private val rutaDataStore: RutaDataStore
//) {
//    suspend fun getRutas(): List<Ruta> {
//        val idEmpresa = userDataStore.getUserIdEmpresa.first() ?: 0
//        val idConductor = userDataStore.getUserId.first() ?: 0
//
//        val response = apiService.getRutas(idEmpresa, idConductor)
//        if (response.isSuccessful) {
//            val rutasResponse = response.body()
//            if (rutasResponse != null) {
//                rutaDataStore.saveRutas(rutasResponse.data)
//                return rutasResponse.data
//            } else {
//                return emptyList()
//            }
//        } else {
//            return emptyList()
//        }
//    }
//
//    suspend fun getRutasPorConductor(idConductor: Int): List<Ruta> {
//        return emptyList()
//    }
//}