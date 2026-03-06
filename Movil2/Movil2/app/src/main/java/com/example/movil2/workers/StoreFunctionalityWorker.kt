package com.example.movil2.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.movil2.data.local.Sicenetdatabase
import com.example.movil2.data.repository.Sicenetlocalrepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class StoreFunctionalityWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val funcType = inputData.getString(FetchFunctionalityWorker.KEY_FUNC_TYPE)
            ?: return@withContext Result.failure()
        Log.d("StoreWorker", "Iniciando guardado para: $funcType")

        try {
            // Leer desde archivo temporal
            val file = File(applicationContext.cacheDir, "temp_$funcType.json")
            if (!file.exists()) {
                Log.e("StoreWorker", "Error: No existe el archivo temp_$funcType.json")
                return@withContext Result.failure()
            }
            
            val resultJson = file.readText()
            file.delete() // limpiar después de leer

            val prefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val matriculaActiva = prefs.getString("matricula_activa", "") ?: ""
            
            if (matriculaActiva.isBlank()) {
                Log.e("StoreWorker", "Error: No hay matrícula activa para guardar")
                return@withContext Result.failure()
            }

            val dao = Sicenetdatabase.getDatabase(applicationContext).sicenetDao()
            val localRepo = Sicenetlocalrepository(dao)

            when (funcType) {
                FetchFunctionalityWorker.FUNC_CARGA          -> localRepo.saveCargaAcademica(matriculaActiva, resultJson)
                FetchFunctionalityWorker.FUNC_KARDEX         -> localRepo.saveKardex(matriculaActiva, resultJson)
                FetchFunctionalityWorker.FUNC_CALIF_UNIDADES -> localRepo.saveCalifUnidades(matriculaActiva, resultJson)
                FetchFunctionalityWorker.FUNC_CALIF_FINAL    -> localRepo.saveCalifFinal(matriculaActiva, resultJson)
                else -> {
                    Log.e("StoreWorker", "Tipo de función desconocido: $funcType")
                    return@withContext Result.failure()
                }
            }

            Log.d("StoreWorker", "Guardado exitoso en DB para $funcType")
            Result.success()
        } catch (e: Exception) {
            Log.e("StoreWorker", "Excepción al guardar $funcType: ${e.message}")
            Result.failure()
        }
    }

    companion object {
        fun workName(funcType: String) = "store_$funcType"
    }
}