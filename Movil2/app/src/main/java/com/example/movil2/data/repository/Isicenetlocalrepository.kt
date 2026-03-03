package com.example.movil2.data.repository

import com.example.movil2.data.local.entity.*

interface Isicenetlocalrepository {

    // Alumno / Perfil
    suspend fun saveAlumno(alumno: AlumnoDB)
    suspend fun getAlumno(): AlumnoDB?

    // Carga académica
    suspend fun saveCargaAcademica(matricula: String, json: String)
    suspend fun getCargaAcademica(matricula: String): CargaAcademicaDB?

    // Kardex
    suspend fun saveKardex(matricula: String, json: String)
    suspend fun getKardex(matricula: String): KardexDB?

    // Calificaciones por unidad
    suspend fun saveCalifUnidades(matricula: String, json: String)
    suspend fun getCalifUnidades(matricula: String): CalifUnidadesDB?

    // Calificación final
    suspend fun saveCalifFinal(matricula: String, json: String)
    suspend fun getCalifFinal(matricula: String): CalifFinalDB?
}