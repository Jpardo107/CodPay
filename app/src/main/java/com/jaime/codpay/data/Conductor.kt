package com.jaime.codpay.data

data class LoginResponse(
    val status: String,
    val message: String?,
    val token: String?,
    val conductor: Conductor?
)

data class Conductor(
    val idConductor: Int,
    val nombreUserB2B: String,
    val apellidosUserB2B: String,
    val emailUserB2B: String,
    val telefonoUserB2B: String,
    val rutUsuarioB2B: String,
    val idEmpresa: Int,
    val estadoUsuarioB2B: Int
)

