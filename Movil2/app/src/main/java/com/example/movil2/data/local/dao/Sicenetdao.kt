package com.example.movil2.data.local.dao

import androidx.room.*
import com.example.movil2.data.local.entity.*

@Dao
interface Sicenetdao {

    // ---- Alumno ----
    @Upsert
    suspend fun upsertAlumno(alumno: AlumnoDB)

    @Query("SELECT * FROM alumno LIMIT 1")
    suspend fun getAlumno(): AlumnoDB?

    // ---- Carga Académica ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCargaAcademica(carga: CargaAcademicaDB)

    @Query("SELECT * FROM carga_academica WHERE matricula = :matricula ORDER BY lastSync DESC LIMIT 1")
    suspend fun getCargaAcademica(matricula: String): CargaAcademicaDB?

    @Query("DELETE FROM carga_academica WHERE matricula = :matricula")
    suspend fun deleteCargaAcademica(matricula: String)

    // ---- Kardex ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKardex(kardex: KardexDB)

    @Query("SELECT * FROM kardex WHERE matricula = :matricula ORDER BY lastSync DESC LIMIT 1")
    suspend fun getKardex(matricula: String): KardexDB?

    @Query("DELETE FROM kardex WHERE matricula = :matricula")
    suspend fun deleteKardex(matricula: String)

    // ---- Calificaciones por Unidad ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalifUnidades(calif: CalifUnidadesDB)

    @Query("SELECT * FROM calif_unidades WHERE matricula = :matricula ORDER BY lastSync DESC LIMIT 1")
    suspend fun getCalifUnidades(matricula: String): CalifUnidadesDB?

    @Query("DELETE FROM calif_unidades WHERE matricula = :matricula")
    suspend fun deleteCalifUnidades(matricula: String)

    // ---- Calificación Final ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalifFinal(calif: CalifFinalDB)

    @Query("SELECT * FROM calif_final WHERE matricula = :matricula ORDER BY lastSync DESC LIMIT 1")
    suspend fun getCalifFinal(matricula: String): CalifFinalDB?

    @Query("DELETE FROM calif_final WHERE matricula = :matricula")
    suspend fun deleteCalifFinal(matricula: String)
}