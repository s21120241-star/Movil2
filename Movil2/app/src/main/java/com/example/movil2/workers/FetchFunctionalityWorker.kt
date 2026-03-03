package com.example.movil2.workers

import android.content.Context
import androidx.work.*
import com.example.movil2.data.remote.RetrofitClient
import com.example.movil2.data.repository.SicenetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker 1 (Funcionalidades):
 * Consulta la funcionalidad indicada desde sicenet.
 * INPUT:  KEY_FUNC_TYPE  → "carga" | "kardex" | "calif_unidades" | "calif_final"
 * OUTPUT: KEY_RESULT_JSON → JSON en string
 *         KEY_FUNC_TYPE   → reenvía el tipo para que el Worker2 sepa qué guardar
 */
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

            val output = workDataOf(
                KEY_RESULT_JSON to resultJson,
                KEY_FUNC_TYPE to funcType
            )
            Result.success(output)
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