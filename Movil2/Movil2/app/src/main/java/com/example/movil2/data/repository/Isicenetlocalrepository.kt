package com.example.movil2.data.repository

import com.example.movil2.data.local.entity.*
import kotlinx.coroutines.flow.Flow

interface Isicenetlocalrepository {

    // Alumno / Perfil
    suspend fun saveAlumno(alumno: AlumnoDB)
    fun getAlumnoFlow(matricula: String): Flow<AlumnoDB?>
    suspend fun getAlumno(matricula: String): AlumnoDB?

    // Carga académica
    suspend fun saveCargaAcademica(matricula: String, json: String)
    fun getCargaAcademicaFlow(matricula: String): Flow<CargaAcademicaDB?>
    suspend fun getCargaAcademica(matricula: String): CargaAcademicaDB?

    // Kardex
    suspend fun saveKardex(matricula: String, json: String)
    fun getKardexFlow(matricula: String): Flow<KardexDB?>
    suspend fun getKardex(matricula: String): KardexDB?

    // Calificaciones por unidad
    suspend fun saveCalifUnidades(matricula: String, json: String)
    fun getCalifUnidadesFlow(matricula: String): Flow<CalifUnidadesDB?>
    suspend fun getCalifUnidades(matricula: String): CalifUnidadesDB?

    // Calificación final
    suspend fun saveCalifFinal(matricula: String, json: String)
    fun getCalifFinalFlow(matricula: String): Flow<CalifFinalDB?>
    suspend fun getCalifFinal(matricula: String): CalifFinalDB?
}