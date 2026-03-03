package com.example.movil2.ui.kardex

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
import org.json.JSONObject

sealed interface KardexUiState {
    object Loading : KardexUiState
    data class Success(
        val items: List<Map<String, String>>,
        val promedio: String,
        val lastSync: String
    ) : KardexUiState
    data class Error(val message: String) : KardexUiState
}

class KardexViewModel(private val context: Context) : ViewModel() {

    private val _uiState = MutableStateFlow<KardexUiState>(KardexUiState.Loading)
    val uiState: StateFlow<KardexUiState> = _uiState

    private val localRepo by lazy {
        Sicenetlocalrepository(Sicenetdatabase.getDatabase(context).sicenetDao())
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = KardexUiState.Loading
            if (NetworkUtils.isOnline(context)) {
                val req = SyncManager.enqueueFunctionalitySync(context, FetchFunctionalityWorker.FUNC_KARDEX)
                WorkManager.getInstance(context).getWorkInfoByIdLiveData(req.id)
                    .observeForever { info ->
                        if (info?.state == WorkInfo.State.SUCCEEDED ||
                            info?.state == WorkInfo.State.FAILED) {
                            viewModelScope.launch { loadFromLocal() }
                        }
                    }
            } else {
                loadFromLocal()
            }
        }
    }

    private suspend fun loadFromLocal() {
        val alumno = localRepo.getAlumno() ?: run {
            _uiState.value = KardexUiState.Error("Sin sesión local"); return
        }
        val db = localRepo.getKardex(alumno.matricula) ?: run {
            _uiState.value = KardexUiState.Error("Sin datos de kardex. Conéctate a internet."); return
        }

        // El JSON puede ser un objeto con campo "kardex" (array) y "promedio"
        // o directamente un array; manejamos ambos casos
        val promedio: String
        val items: List<Map<String, String>>

        try {
            val root = JSONObject(db.jsonData)
            promedio = root.optString("promedio", "N/A")
            val array = root.optJSONArray("kardex") ?: JSONArray()
            items = (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                obj.keys().asSequence().associateWith { key -> obj.optString(key) }
            }
        } catch (e: Exception) {
            // Intentar como array directo
            return try {
                val array = JSONArray(db.jsonData)
                val parsed = (0 until array.length()).map { i ->
                    val obj = array.getJSONObject(i)
                    obj.keys().asSequence().associateWith { key -> obj.optString(key) }
                }
                _uiState.value = KardexUiState.Success(parsed, "N/A", DateUtils.formatTimestamp(db.lastSync))
            } catch (e2: Exception) {
                _uiState.value = KardexUiState.Error("Error al leer kardex")
            }
        }
        _uiState.value = KardexUiState.Success(items, promedio, DateUtils.formatTimestamp(db.lastSync))
    }
}