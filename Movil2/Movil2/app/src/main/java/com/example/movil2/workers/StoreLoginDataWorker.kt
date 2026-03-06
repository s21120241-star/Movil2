package com.example.movil2.workers

import android.content.Context
import androidx.work.*
import com.example.movil2.data.local.Sicenetdatabase
import com.example.movil2.data.local.entity.AlumnoDB
import com.example.movil2.data.repository.Sicenetlocalrepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class StoreLoginDataWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Leer desde archivo temporal
            val file = File(applicationContext.cacheDir, "temp_profile.json")
            if (!file.exists()) return@withContext Result.failure()
            val profileJson = file.readText().trim()
            file.delete()

            if (profileJson.isEmpty()) return@withContext Result.failure()

            // Manejar si el JSON es un objeto { } o un arreglo [ { } ]
            val json = if (profileJson.startsWith("[")) {
                JSONArray(profileJson).getJSONObject(0)
            } else {
                JSONObject(profileJson)
            }

            // Mapeo flexible de campos (soporta minúsculas y Mayúsculas)
            val alumno = AlumnoDB(
                matricula = json.optString("matricula").ifEmpty { json.optString("Matricula") },
                nombre = json.optString("nombre").ifEmpty { json.optString("Nombre") },
                carrera = json.optString("carrera").ifEmpty { json.optString("Carrera") },
                especialidad = json.optString("especialidad").ifEmpty { json.optString("Especialidad") },
                semActual = json.optInt("semActual", json.optInt("SemActual", 0)),
                cdtosAcumulados = json.optInt("cdtosAcumulados", json.optInt("CdtosAcumulados", 0)),
                cdtosActuales = json.optInt("cdtosActuales", json.optInt("CdtosActuales", 0)),
                estatus = json.optString("estatus").ifEmpty { json.optString("Estatus") },
                fechaReins = json.optString("fechaReins").ifEmpty { json.optString("FechaReins") },
                urlFoto = json.optString("urlFoto").ifEmpty { json.optString("UrlFoto") },
                lastSync = System.currentTimeMillis()
            )

            if (alumno.matricula.isEmpty()) return@withContext Result.failure()

            // Guardar matrícula activa para que el perfil sepa qué buscar
            val prefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("matricula_activa", alumno.matricula).apply()

            val dao = Sicenetdatabase.getDatabase(applicationContext).sicenetDao()
            val localRepo = Sicenetlocalrepository(dao)
            localRepo.saveAlumno(alumno)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "store_login_data"
    }
}