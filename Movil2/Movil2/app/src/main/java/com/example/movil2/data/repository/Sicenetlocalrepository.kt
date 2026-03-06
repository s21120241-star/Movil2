package com.example.movil2.data.repository

import com.example.movil2.data.local.dao.Sicenetdao
import com.example.movil2.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class Sicenetlocalrepository(private val dao: Sicenetdao) : Isicenetlocalrepository {

    override suspend fun saveAlumno(alumno: AlumnoDB) = dao.upsertAlumno(alumno)
    override fun getAlumnoFlow(matricula: String): Flow<AlumnoDB?> = dao.getAlumnoFlow(matricula)
    override suspend fun getAlumno(matricula: String): AlumnoDB? = dao.getAlumno(matricula)

    override suspend fun saveCargaAcademica(matricula: String, json: String) {
        dao.deleteCargaAcademica(matricula)
        dao.insertCargaAcademica(CargaAcademicaDB(matricula = matricula, jsonData = json))
    }
    override fun getCargaAcademicaFlow(matricula: String): Flow<CargaAcademicaDB?> = dao.getCargaAcademicaFlow(matricula)
    override suspend fun getCargaAcademica(matricula: String) = dao.getCargaAcademica(matricula)

    override suspend fun saveKardex(matricula: String, json: String) {
        dao.deleteKardex(matricula)
        dao.insertKardex(KardexDB(matricula = matricula, jsonData = json))
    }
    override fun getKardexFlow(matricula: String): Flow<KardexDB?> = dao.getKardexFlow(matricula)
    override suspend fun getKardex(matricula: String) = dao.getKardex(matricula)

    override suspend fun saveCalifUnidades(matricula: String, json: String) {
        dao.deleteCalifUnidades(matricula)
        dao.insertCalifUnidades(CalifUnidadesDB(matricula = matricula, jsonData = json))
    }
    override fun getCalifUnidadesFlow(matricula: String): Flow<CalifUnidadesDB?> = dao.getCalifUnidadesFlow(matricula)
    override suspend fun getCalifUnidades(matricula: String) = dao.getCalifUnidades(matricula)

    override suspend fun saveCalifFinal(matricula: String, json: String) {
        dao.deleteCalifFinal(matricula)
        dao.insertCalifFinal(CalifFinalDB(matricula = matricula, jsonData = json))
    }
    override fun getCalifFinalFlow(matricula: String): Flow<CalifFinalDB?> = dao.getCalifFinalFlow(matricula)
    override suspend fun getCalifFinal(matricula: String) = dao.getCalifFinal(matricula)
}