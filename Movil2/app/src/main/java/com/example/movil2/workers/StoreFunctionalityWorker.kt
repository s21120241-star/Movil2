package com.example.movil2.workers

import android.content.Context
import androidx.work.*
import com.example.movil2.data.local.Sicenetdatabase
import com.example.movil2.data.repository.Sicenetlocalrepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker 2 (Funcionalidades):
 * Recibe el JSON de FetchFunctionalityWorker y lo guarda en la BD local
 * según el tipo de funcionalidad.
 */
class StoreFunctionalityWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val funcType   = inputData.getString(FetchFunctionalityWorker.KEY_FUNC_TYPE)
                ?: return@withContext Result.failure()
            val resultJson = inputData.getString(FetchFunctionalityWorker.KEY_RESULT_JSON)
                ?: return@withContext Result.failure()

            val dao       = Sicenetdatabase.getDatabase(applicationContext).sicenetDao()
            val localRepo = Sicenetlocalrepository(dao)

            // Necesitamos la matrícula del alumno guardado
            val alumno = localRepo.getAlumno() ?: return@withContext Result.failure()
            val matricula = alumno.matricula

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