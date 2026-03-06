package com.example.movil2.sync

import android.content.Context
import androidx.work.*
import com.example.movil2.workers.*

object SyncManager {

    fun enqueueLoginSync(context: Context): OneTimeWorkRequest {
        val fetchRequest = OneTimeWorkRequest.Builder(FetchLoginDataWorker::class.java)
            .addTag(FetchLoginDataWorker.WORK_NAME)
            .build()

        val storeRequest = OneTimeWorkRequest.Builder(StoreLoginDataWorker::class.java)
            .addTag(StoreLoginDataWorker.WORK_NAME)
            .build()

        WorkManager.getInstance(context)
            .beginUniqueWork(
                FetchLoginDataWorker.WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                fetchRequest
            )
            .then(storeRequest)
            .enqueue()

        return storeRequest
    }

    fun enqueueFunctionalitySync(context: Context, funcType: String): OneTimeWorkRequest {
        val inputData = workDataOf(FetchFunctionalityWorker.KEY_FUNC_TYPE to funcType)

        val fetchRequest = OneTimeWorkRequest.Builder(FetchFunctionalityWorker::class.java)
            .setInputData(inputData)
            .addTag(FetchFunctionalityWorker.workName(funcType))
            .build()

        val storeRequest = OneTimeWorkRequest.Builder(StoreFunctionalityWorker::class.java)
            .setInputData(inputData) // Importante pasar los datos al siguiente worker
            .addTag(StoreFunctionalityWorker.workName(funcType))
            .build()

        WorkManager.getInstance(context)
            .beginUniqueWork(
                FetchFunctionalityWorker.workName(funcType),
                ExistingWorkPolicy.REPLACE,
                fetchRequest
            )
            .then(storeRequest)
            .enqueue()

        return storeRequest
    }
}