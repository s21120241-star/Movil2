package com.example.movil2.data.repository

import com.example.movil2.data.remote.RetrofitClient
import com.example.movil2.data.remote.SoapRequestBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SicenetRepository : ISicenetRepository {

    override suspend fun login(matricula: String, contrasenia: String, tipoUsuario: String): Boolean {
        return withContext(Dispatchers.IO) {
            val body = SoapRequestBuilder.buildLoginBody(matricula, contrasenia, tipoUsuario)
            val response = RetrofitClient.service.login(body)
            if (response.isSuccessful) {
                val responseBody = response.body()?.string()
                val start = responseBody?.indexOf("<accesoLoginResult>")
                    ?.plus("<accesoLoginResult>".length) ?: -1
                val end = responseBody?.indexOf("</accesoLoginResult>") ?: -1
                if (start != -1 && end != -1 && end > start) {
                    try {
                        val json = JSONObject(responseBody!!.substring(start, end))
                        json.optBoolean("acceso", false)
                    } catch (e: Exception) { false }
                } else false
            } else false
        }
    }

    override suspend fun getProfile(): String? {
        return withContext(Dispatchers.IO) {
            val body = SoapRequestBuilder.buildProfileBody()
            val response = RetrofitClient.service.getProfile(body)
            extractResult(response.body()?.string(),
                "<getAlumnoAcademicoWithLineamientoResult>",
                "</getAlumnoAcademicoWithLineamientoResult>")
        }
    }

    override suspend fun getCargaAcademica(): String? {
        return withContext(Dispatchers.IO) {
            val body = SoapRequestBuilder.buildCargaAcademicaBody()
            val response = RetrofitClient.service.getCargaAcademica(body)
            extractResult(response.body()?.string(),
                "<getCargaAcademicaByAlumnoResult>",
                "</getCargaAcademicaByAlumnoResult>")
        }
    }

    override suspend fun getKardex(): String? {
        return withContext(Dispatchers.IO) {
            val body = SoapRequestBuilder.buildKardexBody()
            val response = RetrofitClient.service.getKardex(body)
            extractResult(response.body()?.string(),
                "<getAllKardexConPromedioByAlumnoResult>",
                "</getAllKardexConPromedioByAlumnoResult>")
        }
    }

    override suspend fun getCalifUnidades(): String? {
        return withContext(Dispatchers.IO) {
            val body = SoapRequestBuilder.buildCalifUnidadesBody()
            val response = RetrofitClient.service.getCalifUnidades(body)
            extractResult(response.body()?.string(),
                "<getCalifUnidadesByAlumnoResult>",
                "</getCalifUnidadesByAlumnoResult>")
        }
    }

    override suspend fun getCalifFinal(): String? {
        return withContext(Dispatchers.IO) {
            val body = SoapRequestBuilder.buildCalifFinalBody()
            val response = RetrofitClient.service.getCalifFinal(body)
            extractResult(response.body()?.string(),
                "<getAllCalifFinalByAlumnosResult>",
                "</getAllCalifFinalByAlumnosResult>")
        }
    }

    private fun extractResult(xml: String?, openTag: String, closeTag: String): String? {
        if (xml == null) return null
        val start = xml.indexOf(openTag).takeIf { it != -1 }?.plus(openTag.length) ?: return null
        val end = xml.indexOf(closeTag).takeIf { it != -1 } ?: return null
        return if (end > start) xml.substring(start, end).trim() else null
    }
}