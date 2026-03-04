package com.example.movil2.ui.califunidades

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movil2.data.local.Sicenetdatabase
import com.example.movil2.data.repository.Sicenetlocalrepository
import com.example.movil2.sync.SyncManager
import com.example.movil2.utils.DateUtils
import com.example.movil2.utils.NetworkUtils
import com.example.movil2.workers.FetchFunctionalityWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray

sealed interface CalifUnidadesUiState {
    object Loading : CalifUnidadesUiState
    data class Success(val items: List<Map<String, String>>, val lastSync: String) : CalifUnidadesUiState
    data class Error(val message: String) : CalifUnidadesUiState
}

class CalifUnidadesViewModel(private val context: Context) : ViewModel() {

    private val _uiState = MutableStateFlow<CalifUnidadesUiState>(CalifUnidadesUiState.Loading)
    val uiState: StateFlow<CalifUnidadesUiState> = _uiState

    private val localRepo by lazy {
        Sicenetlocalrepository(Sicenetdatabase.getDatabase(context).sicenetDao())
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = CalifUnidadesUiState.Loading
            if (NetworkUtils.isOnline(context)) {
                SyncManager.enqueueFunctionalitySync(context, FetchFunctionalityWorker.FUNC_CALIF_UNIDADES)
                delay(3000)
            }
            loadFromLocal()
        }
    }

    private suspend fun loadFromLocal() {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val matricula = prefs.getString("matricula_activa", null) ?: "default"
        val db = localRepo.getCalifUnidades(matricula) ?: run {
            _uiState.value = CalifUnidadesUiState.Error("Sin datos. Conéctate a internet."); return
        }
        val items = try {
            val array = JSONArray(db.jsonData)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                obj.keys().asSequence().associateWith { key -> obj.optString(key) }
            }
        } catch (e: Exception) { emptyList() }
        _uiState.value = CalifUnidadesUiState.Success(items, DateUtils.formatTimestamp(db.lastSync))
    }
}