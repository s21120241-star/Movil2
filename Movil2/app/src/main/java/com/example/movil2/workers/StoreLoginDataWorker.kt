package com.example.movil2.workers

import android.content.Context
import androidx.work.*
import com.example.movil2.data.local.SicenetDatabase
import com.example.movil2.data.local.entity.AlumnoDB
import com.example.movil2.data.repository.Sicenetlocalrepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Worker 2 (Login sync):
 * Recibe el JSON del perfil desde FetchLoginDataWorker y lo almacena en la BD local.
 */
class StoreLoginDataWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val profileJson = inputData.getString(FetchLoginDataWorker.KEY_PROFILE_JSON)
                ?: return@withContext Result.failure()

            val json = JSONObject(profileJson)
            val alumno = AlumnoDB(
                matricula = json.optString("matricula"),
                nombre = json.optString("nombre"),
                carrera = json.optString("carrera"),
                especialidad = json.optString("especialidad"),
                semActual = json.optInt("semActual"),
                cdtosAcumulados = json.optInt("cdtosAcumulados"),
                cdtosActuales = json.optInt("cdtosActuales"),
                estatus = json.optString("estatus"),
                fechaReins = json.optString("fechaReins"),
                urlFoto = json.optString("urlFoto"),
                lastSync = System.currentTimeMillis()
            )

            val dao = SicenetDatabase.getDatabase(applicationContext).sicenetDao()
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