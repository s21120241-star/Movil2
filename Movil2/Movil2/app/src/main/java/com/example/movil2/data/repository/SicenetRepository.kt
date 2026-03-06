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
            val response = try { RetrofitClient.service.login(body) } catch (e: Exception) { null }
            if (response != null && response.isSuccessful) {
                val responseBody = response.body()?.string()
                val result = extractResult(responseBody, "accesoLoginResult")
                if (result != null) {
                    try {
                        val json = JSONObject(result)
                        json.optBoolean("acceso", false)
                    } catch (e: Exception) { false }
                } else false
            } else false
        }
    }

    override suspend fun getProfile(): String? {
        return withContext(Dispatchers.IO) {
            val body = SoapRequestBuilder.buildProfileBody()
            val response = try { RetrofitClient.service.getProfile(body) } catch (e: Exception) { null }
            extractResult(response?.body()?.string(), "getAlumnoAcademicoWithLineamientoResult")
        }
    }

    override suspend fun getCargaAcademica(): String? {
        return withContext(Dispatchers.IO) {
            val body = SoapRequestBuilder.buildCargaAcademicaBody()
            val response = try { RetrofitClient.service.getCargaAcademica(body) } catch (e: Exception) { null }
            extractResult(response?.body()?.string(), "getCargaAcademicaByAlumnoResult")
        }
    }

    override suspend fun getKardex(): String? {
        return withContext(Dispatchers.IO) {
            val body = SoapRequestBuilder.buildKardexBody()
            val response = try { RetrofitClient.service.getKardex(body) } catch (e: Exception) { null }
            extractResult(response?.body()?.string(), "getAllKardexConPromedioByAlumnoResult")
        }
    }

    override suspend fun getCalifUnidades(): String? {
        return withContext(Dispatchers.IO) {
            val body = SoapRequestBuilder.buildCalifUnidadesBody()
            val response = try { RetrofitClient.service.getCalifUnidades(body) } catch (e: Exception) { null }
            extractResult(response?.body()?.string(), "getCalifUnidadesByAlumnoResult")
        }
    }

    override suspend fun getCalifFinal(): String? {
        return withContext(Dispatchers.IO) {
            val body = SoapRequestBuilder.buildCalifFinalBody()
            val response = try { RetrofitClient.service.getCalifFinal(body) } catch (e: Exception) { null }
            extractResult(response?.body()?.string(), "getAllCalifFinalByAlumnosResult")
        }
    }

    private fun extractResult(xml: String?, tagName: String): String? {
        if (xml == null || xml.isBlank()) return null
        try {
            // Busqueda por regex para ignorar prefijos como soap: o ns1:
            val regexOpen = "<(?:\\w+:)?$tagName(?:\\s+[^>]*?)?>".toRegex()
            val matchOpen = regexOpen.find(xml) ?: return null
            val start = matchOpen.range.last + 1
            
            val regexClose = "</(?:\\w+:)?$tagName>".toRegex()
            val matchClose = regexClose.find(xml, start) ?: return null
            val end = matchClose.range.first
            
            var content = xml.substring(start, end).trim()
            
            // Limpieza recursiva de caracteres de escape
            while (content.contains("&lt;") || content.contains("&gt;")) {
                content = content.replace("&lt;", "<")
                                .replace("&gt;", ">")
                                .replace("&quot;", "\"")
                                .replace("&amp;", "&")
            }
            
            // Si el contenido sigue teniendo XML dentro (como diffgram), lo extraemos
            if (content.contains("<") && content.contains(">")) {
                // Buscamos cualquier cosa que parezca un JSON [...] o {...} dentro del XML extraido
                val jsonStart = content.indexOfAny(listOf("[", "{"))
                val jsonEnd = content.lastIndexOfAny(listOf("]", "}"))
                if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                    return content.substring(jsonStart, jsonEnd + 1)
                }
            }
            
            return content
        } catch (e: Exception) { return null }
    }
}