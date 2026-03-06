package com.example.movil2.workers

import android.content.Context
import androidx.work.*
import com.example.movil2.data.remote.RetrofitClient
import com.example.movil2.data.repository.SicenetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FetchLoginDataWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            RetrofitClient.init(applicationContext)
            val repository = SicenetRepository()

            val profileJson = repository.getProfile()
                ?: return@withContext Result.failure()

            // Guardar en archivo temporal
            val file = File(applicationContext.cacheDir, "temp_profile.json")
            file.writeText(profileJson)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_PROFILE_JSON = "profile_json"
        const val WORK_NAME = "fetch_login_data"
    }
}