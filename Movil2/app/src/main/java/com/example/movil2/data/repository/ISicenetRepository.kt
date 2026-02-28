package com.example.movil2.data.repository

interface ISicenetRepository {
    suspend fun login(matricula: String, contrasenia: String, tipoUsuario: String): Boolean
    suspend fun getProfile(): String?
}