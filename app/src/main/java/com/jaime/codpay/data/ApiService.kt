package com.jaime.codpay.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @POST("loginConductor.php") // Endpoint para el login
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse> // Devuelve un objeto LoginResponse

    @GET("envios.php?recurso=rutas")
    suspend fun getRutas(@Query("idEmpresaB2B") idEmpresa: Int): Response<RutasResponse>

}