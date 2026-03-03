package com.example.movil2.ui.carga

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.movil2.data.local.Sicenetdatabase
import com.example.movil2.data.repository.Sicenetlocalrepository
import com.example.movil2.sync.SyncManager
import com.example.movil2.utils.DateUtils
import com.example.movil2.utils.NetworkUtils
import com.example.movil2.workers.FetchFunctionalityWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray

sealed interface CargaUiState {
    object Loading : CargaUiState
    data class Success(val items: List<Map<String, String>>, val lastSync: String) : CargaUiState
    data class Error(val message: String) : CargaUiState
}

class CargaAcademicaViewModel(private val context: Context) : ViewModel() {

    private val _uiState = MutableStateFlow<CargaUiState>(CargaUiState.Loading)
    val uiState: StateFlow<CargaUiState> = _uiState

    private val localRepo: Sicenetlocalrepository by lazy {
        Sicenetlocalrepository(Sicenetdatabase.getDatabase(context).sicenetDao())
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = CargaUiState.Loading

            if (NetworkUtils.isOnline(context)) {
                val fetchRequest = SyncManager.enqueueFunctionalitySync(
                    context, FetchFunctionalityWorker.FUNC_CARGA
                )
                WorkManager.getInstance(context)
                    .getWorkInfoByIdLiveData(fetchRequest.id)
                    .observeForever { info ->
                        if (info?.state == WorkInfo.State.SUCCEEDED) {
                            viewModelScope.launch { loadFromLocal() }
                        } else if (info?.state == WorkInfo.State.FAILED) {
                            viewModelScope.launch { loadFromLocal() }
                        }
                    }
            } else {
                loadFromLocal()
            }
        }
    }

    private suspend fun loadFromLocal() {
        val alumno = localRepo.getAlumno()
        if (alumno == null) {
            _uiState.value = CargaUiState.Error("No hay datos locales disponibles")
            return
        }
        val db = localRepo.getCargaAcademica(alumno.matricula)
        if (db == null) {
            _uiState.value = CargaUiState.Error("Sin datos de carga académica. Conéctate a internet.")
            return
        }
        val items = parseJsonArray(db.jsonData)
        _uiState.value = CargaUiState.Success(items, DateUtils.formatTimestamp(db.lastSync))
    }

    private fun parseJsonArray(json: String): List<Map<String, String>> {
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                obj.keys().asSequence().associateWith { key -> obj.optString(key) }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}