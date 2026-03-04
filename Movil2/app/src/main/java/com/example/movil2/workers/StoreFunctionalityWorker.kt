package com.example.movil2.workers

import android.content.Context
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
        try {
            val funcType = inputData.getString(FetchFunctionalityWorker.KEY_FUNC_TYPE)
                ?: return@withContext Result.failure()

            // Leer desde archivo temporal
            val file = File(applicationContext.cacheDir, "temp_$funcType.json")
            if (!file.exists()) return@withContext Result.failure()
            val resultJson = file.readText()
            file.delete() // limpiar después de leer

            val dao = Sicenetdatabase.getDatabase(applicationContext).sicenetDao()
            val localRepo = Sicenetlocalrepository(dao)
            val matricula = localRepo.getAlumno()?.matricula ?: "default"

            when (funcType) {
                FetchFunctionalityWorker.FUNC_CARGA          -> localRepo.saveCargaAcademica(matricula, resultJson)
                FetchFunctionalityWorker.FUNC_KARDEX         -> localRepo.saveKardex(matricula, resultJson)
                FetchFunctionalityWorker.FUNC_CALIF_UNIDADES -> localRepo.saveCalifUnidades(matricula, resultJson)
                FetchFunctionalityWorker.FUNC_CALIF_FINAL    -> localRepo.saveCalifFinal(matricula, resultJson)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        fun workName(funcType: String) = "store_$funcType"
    }
}