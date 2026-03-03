package com.example.movil2.data.repository

import com.example.movil2.data.local.dao.Sicenetdao
import com.example.movil2.data.local.entity.*

class Sicenetlocalrepository(private val dao: Sicenetdao) : Isicenetlocalrepository {

    override suspend fun saveAlumno(alumno: AlumnoDB) = dao.upsertAlumno(alumno)
    override suspend fun getAlumno(): AlumnoDB? = dao.getAlumno()

    override suspend fun saveCargaAcademica(matricula: String, json: String) {
        dao.deleteCargaAcademica(matricula)
        dao.insertCargaAcademica(CargaAcademicaDB(matricula = matricula, jsonData = json))
    }
    override suspend fun getCargaAcademica(matricula: String) = dao.getCargaAcademica(matricula)

    override suspend fun saveKardex(matricula: String, json: String) {
        dao.deleteKardex(matricula)
        dao.insertKardex(KardexDB(matricula = matricula, jsonData = json))
    }
    override suspend fun getKardex(matricula: String) = dao.getKardex(matricula)

    override suspend fun saveCalifUnidades(matricula: String, json: String) {
        dao.deleteCalifUnidades(matricula)
        dao.insertCalifUnidades(CalifUnidadesDB(matricula = matricula, jsonData = json))
    }
    override suspend fun getCalifUnidades(matricula: String) = dao.getCalifUnidades(matricula)

    override suspend fun saveCalifFinal(matricula: String, json: String) {
        dao.deleteCalifFinal(matricula)
        dao.insertCalifFinal(CalifFinalDB(matricula = matricula, jsonData = json))
    }
    override suspend fun getCalifFinal(matricula: String) = dao.getCalifFinal(matricula)
}