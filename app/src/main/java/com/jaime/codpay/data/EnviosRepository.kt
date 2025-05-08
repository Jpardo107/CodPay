package com.jaime.codpay.data

import kotlinx.coroutines.flow.Flow

interface EnviosRepository {
    fun getEnvios(idEmpresa: Int): Flow<List<Envio>>
}