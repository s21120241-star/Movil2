package com.example.movil2.workers

import android.content.Context
import android.util.Log
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
        val funcType = inputData.getString(KEY_FUNC_TYPE) ?: return@withContext Result.failure()
        Log.d("FetchWorker", "Iniciando descarga para: $funcType")

        try {
            RetrofitClient.init(applicationContext)
            val repository = SicenetRepository()
            
            val prefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val matricula = prefs.getString("matricula_activa", "") ?: ""

            if (matricula.isEmpty()) {
                Log.e("FetchWorker", "Error: No hay matrícula activa")
                return@withContext Result.failure()
            }

            // Llamadas corregidas: Se quitó el parámetro 'matricula' porque el servidor 
            // de Sicenet identifica al usuario mediante la sesión (cookies).
            val resultJson: String? = when (funcType) {
                FUNC_CARGA          -> repository.getCargaAcademica()
                FUNC_KARDEX         -> repository.getKardex()
                FUNC_CALIF_UNIDADES -> repository.getCalifUnidades()
                FUNC_CALIF_FINAL    -> repository.getCalifFinal()
                else                -> null
            }

            if (resultJson == null || resultJson.isBlank()) {
                Log.e("FetchWorker", "Error: El servidor devolvió datos vacíos para $funcType")
                return@withContext Result.failure()
            }

            // Guardar en archivo temporal
            val file = File(applicationContext.cacheDir, "temp_$funcType.json")
            file.writeText(resultJson)
            Log.d("FetchWorker", "Datos guardados en cache para $funcType")

            Result.success(workDataOf(KEY_FUNC_TYPE to funcType))
        } catch (e: Exception) {
            Log.e("FetchWorker", "Excepción en $funcType: ${e.message}")
            Result.failure()
        }
    }

    companion object {
        const val KEY_FUNC_TYPE = "func_type"
        const val FUNC_CARGA = "carga"
        const val FUNC_KARDEX = "kardex"
        const val FUNC_CALIF_UNIDADES = "calif_unidades"
        const val FUNC_CALIF_FINAL = "calif_final"

        fun workName(funcType: String) = "fetch_$funcType"
    }
}