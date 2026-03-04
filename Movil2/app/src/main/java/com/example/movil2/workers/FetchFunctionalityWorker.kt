package com.example.movil2.workers

import android.content.Context
import androidx.work.*
import com.example.movil2.data.remote.RetrofitClient
import com.example.movil2.data.repository.SicenetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FetchFunctionalityWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            RetrofitClient.init(applicationContext)
            val repository = SicenetRepository()
            val funcType = inputData.getString(KEY_FUNC_TYPE)
                ?: return@withContext Result.failure()

            val resultJson: String? = when (funcType) {
                FUNC_CARGA          -> repository.getCargaAcademica()
                FUNC_KARDEX         -> repository.getKardex()
                FUNC_CALIF_UNIDADES -> repository.getCalifUnidades()
                FUNC_CALIF_FINAL    -> repository.getCalifFinal()
                else                -> null
            }

            if (resultJson == null) return@withContext Result.failure()

            // Guardar en archivo temporal — evita límite de 10KB de WorkManager
            val file = File(applicationContext.cacheDir, "temp_$funcType.json")
            file.writeText(resultJson)

            // Solo pasa el tipo como output
            Result.success(workDataOf(KEY_FUNC_TYPE to funcType))
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_FUNC_TYPE    = "func_type"
        const val KEY_RESULT_JSON  = "result_json"

        const val FUNC_CARGA          = "carga"
        const val FUNC_KARDEX         = "kardex"
        const val FUNC_CALIF_UNIDADES = "calif_unidades"
        const val FUNC_CALIF_FINAL    = "calif_final"

        fun workName(funcType: String) = "fetch_$funcType"
    }
}