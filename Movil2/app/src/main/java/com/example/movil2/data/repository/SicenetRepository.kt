package com.example.movil2.data.repository

import com.example.movil2.data.remote.RetrofitClient
import com.example.movil2.data.remote.SoapRequestBuilder
import com.example.movil2.model.Alumno
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject


class SicenetRepository {


    suspend fun login(matricula: String, contrasenia: String, tipoUsuario: String): Boolean {
        return withContext(Dispatchers.IO) {
            val body = SoapRequestBuilder.buildLoginBody(matricula, contrasenia, tipoUsuario)
            val response = RetrofitClient.service.login(body)

            if (response.isSuccessful) {
                val responseBody = response.body()?.string()
                println("RESPUESTA LOGIN:\n$responseBody")

                val start =
                    responseBody?.indexOf("<accesoLoginResult>")?.plus("<accesoLoginResult>".length)
                        ?: -1
                val end = responseBody?.indexOf("</accesoLoginResult>") ?: -1

                if (start != -1 && end != -1 && end > start) {
                    val jsonString = responseBody!!.substring(start, end)
                    println("JSON dentro de accesoLoginResult: $jsonString")

                    try {
                        val json = JSONObject(jsonString)
                        val acceso = json.optBoolean("acceso", false)
                        acceso
                    } catch (e: Exception) {
                        println("Error al parsear JSON: ${e.message}")
                        false
                    }
                } else {
                    println("No se encontró accesoLoginResult en la respuesta")
                    false
                }
            } else {
                println("ERROR HTTP: ${response.code()}")
                false
            }
        }
    }


    suspend fun getProfile(): String? {
        return withContext(Dispatchers.IO) {
            val body = SoapRequestBuilder.buildProfileBody()
            val response = RetrofitClient.service.getProfile("", body)

            if (response.isSuccessful) {
                val responseBody = response.body()?.string()

                val start = responseBody?.indexOf("<getAlumnoAcademicoWithLineamientoResult>")
                    ?.plus("<getAlumnoAcademicoWithLineamientoResult>".length) ?: -1
                val end = responseBody?.indexOf("</getAlumnoAcademicoWithLineamientoResult>") ?: -1

                if (start != -1 && end > start) {
                    val jsonString = responseBody!!.substring(start, end).trim()
                    println("JSON INTERNO DEL PERFIL:\n$jsonString")
                    jsonString
                } else null
            } else null
        }
    }
}
