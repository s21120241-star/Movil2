package com.example.movil2.sync

import android.content.Context
import androidx.work.*
import com.example.movil2.workers.*

object SyncManager {

    /**
     * Encola la cadena de Workers para Login + Perfil.
     * Llamar justo después de autenticarse exitosamente con internet.
     */
    fun enqueueLoginSync(context: Context): OneTimeWorkRequest {
        val fetchRequest = OneTimeWorkRequest.Builder(FetchLoginDataWorker::class.java)
            .setConstraints(Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build())
            .addTag(FetchLoginDataWorker.WORK_NAME)
            .build()

        val storeRequest = OneTimeWorkRequest.Builder(StoreLoginDataWorker::class.java)
            .addTag(StoreLoginDataWorker.WORK_NAME)
            .build()

        WorkManager.getInstance(context)
            .beginUniqueWork(
                FetchLoginDataWorker.WORK_NAME,
                ExistingWorkPolicy.KEEP,
                fetchRequest
            )
            .then(storeRequest)
            .enqueue()

        return fetchRequest
    }

    /**
     * Encola la cadena de Workers para una funcionalidad específica.
     * @param funcType  uno de FetchFunctionalityWorker.FUNC_*
     */
    fun enqueueFunctionalitySync(context: Context, funcType: String): OneTimeWorkRequest {
        val inputData = workDataOf(FetchFunctionalityWorker.KEY_FUNC_TYPE to funcType)

        val fetchRequest = OneTimeWorkRequest.Builder(FetchFunctionalityWorker::class.java)
            .setInputData(inputData)
            .setConstraints(Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build())
            .addTag(FetchFunctionalityWorker.workName(funcType))
            .build()

        val storeRequest = OneTimeWorkRequest.Builder(StoreFunctionalityWorker::class.java)
            .addTag(StoreFunctionalityWorker.workName(funcType))
            .build()

        WorkManager.getInstance(context)
            .beginUniqueWork(
                FetchFunctionalityWorker.workName(funcType),
                ExistingWorkPolicy.KEEP,
                fetchRequest
            )
            .then(storeRequest)
            .enqueue()

        return fetchRequest
    }
}