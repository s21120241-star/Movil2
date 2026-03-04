package com.example.movil2.ui.kardex

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
                SyncManager.enqueueFunctionalitySync(context, FetchFunctionalityWorker.FUNC_KARDEX)
                delay(3000)
            }
            loadFromLocal()
        }
    }

    private suspend fun loadFromLocal() {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val matricula = prefs.getString("matricula_activa", null) ?: "default"
        val db = localRepo.getKardex(matricula) ?: run {
            _uiState.value = KardexUiState.Error("Sin datos de kardex. Conéctate a internet."); return
        }
        try {
            val root = JSONObject(db.jsonData)
            val promedio = root.optString("promedio", "N/A")
            val array = root.optJSONArray("kardex") ?: JSONArray()
            val items = (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                obj.keys().asSequence().associateWith { key -> obj.optString(key) }
            }
            _uiState.value = KardexUiState.Success(items, promedio, DateUtils.formatTimestamp(db.lastSync))
        } catch (e: Exception) {
            try {
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
    }
}