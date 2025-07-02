package com.jaime.codpay.data

import android.util.Log
import kotlinx.coroutines.flow.Flow

interface EnviosRepository {
    fun getEnvios(idEmpresa: Int): Flow<List<Envio>>
    suspend fun actualizarEstadoEnvio(idEnvio: Int, estado: String): Boolean
    suspend fun reagendarEnvio(idEnvio: Int, nuevaFecha: String): Boolean
}