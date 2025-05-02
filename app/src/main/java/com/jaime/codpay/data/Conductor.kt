package com.jaime.codpay.data

data class LoginResponse(
    val status: String,
    val token: String,
    val conductor: Conductor
)

data class Conductor(
    val idUsuarioB2B: Int,
    val nombreUserB2B: String,
    val apellidoUserB2B: String,
    val emailUserB2B: String,
    val telefonoUserB2B: String,
    val rutUsuarioB2B: String,
    val idEmpresa: Int,
    val estadoUsuarioB2B: Int
)

