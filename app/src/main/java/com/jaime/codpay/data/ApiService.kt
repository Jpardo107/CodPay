package com.jaime.codpay.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

    @POST("loginConductor.php") // Endpoint para el login
    fun login(@Body loginRequest: LoginRequest): Call<MfaPendingResponse> // Devuelve un objeto LoginResponse

    @POST("validarMFAconductor.php")
    fun validarMFA(@Body request: MfaValidationRequest): Call<LoginResponse>


    @GET("envios.php?recurso=rutas")
    suspend fun getRutas(
        @Query("idEmpresaB2B") idEmpresa: Int,
        @Query("idConductor") idConductor: Int
    ): Response<RutasResponse>

    @GET("envios.php?recurso=paquetesPorRuta")
    suspend fun getPaquetesPorRuta(
        @Query("idRuta") idRuta: Int
    ): Response<PaquetesResponse>

    @GET("envios.php?recurso=envios")
    suspend fun getEnvios(@Query("idEmpresaB2B") idEmpresa: Int): Response<EnvioResponse>

    @POST("pagosPedidos.php")
    suspend fun registrarPago(
        @Body pagoRequest: PagoRequest
    ): Response<PagoResponse>

    @GET("pedidos.php")
    suspend fun getPedidos(@Query("idEmpresaB2B") idEmpresa: Int): Response<PedidosResponse>

    @PUT("envios.php?recurso=rutas")
    suspend fun actualizarRuta(@Body body: ActualizarRutaRequest): Response<Any>

    @PUT("envios.php?recurso=envios")
    suspend fun actualizarEstadoEnvio(@Body envio: EnvioActualizacionRequest): Response<Unit>

    @PUT("envios.php?recurso=envios")
    suspend fun reagendarEnvio(@Body envio: EnvioReagendamientoRequest): Response<Unit>


    @PUT("envios.php?recurso=rutas")
    suspend fun actualizarEstadoRuta(@Body body: ActualizarEstadoRutaRequest): Response<Unit>


}