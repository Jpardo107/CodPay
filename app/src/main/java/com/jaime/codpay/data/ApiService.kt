package com.jaime.codpay.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.jaime.codpay.data.LoginResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @POST("loginConductor.php") // Endpoint para el login
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse> // Devuelve un objeto LoginResponse

    @GET("envios.php?recurso=rutas&idEmpresaB2B=58")
    suspend fun getRutas(): Response<RutasResponse>
}