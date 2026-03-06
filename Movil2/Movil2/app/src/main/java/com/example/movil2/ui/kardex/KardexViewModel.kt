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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

sealed interface KardexUiState {
    object Loading : KardexUiState
    data class Success(
        val items: List<Map<String, String>>, 
        val promedio: String, 
        val aprobadas: String,
        val totales: String,
        val creditos: String,
        val lastSync: String
    ) : KardexUiState
    data class Error(val message: String) : KardexUiState
}

class KardexViewModel(private val context: Context) : ViewModel() {

    private val localRepo by lazy {
        Sicenetlocalrepository(Sicenetdatabase.getDatabase(context).sicenetDao())
    }

    private val _uiState = MutableStateFlow<KardexUiState>(KardexUiState.Loading)
    val uiState: StateFlow<KardexUiState> = _uiState

    init { observeLocalData() }

    private fun observeLocalData() {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val matricula = prefs.getString("matricula_activa", "") ?: ""
        
        if (matricula.isNotEmpty()) {
            localRepo.getKardexFlow(matricula)
                .onEach { db ->
                    if (db != null) {
                        try {
                            val rawData = db.jsonData.trim()
                            val items = parseKardexAggressively(rawData)
                            val summary = extractSummaryData(rawData)
                            
                            _uiState.value = KardexUiState.Success(
                                items = items,
                                promedio = summary["promedio"] ?: "0.0",
                                aprobadas = summary["aprobadas"] ?: "0",
                                totales = summary["totales"] ?: "0",
                                creditos = summary["creditos"] ?: "0",
                                lastSync = DateUtils.formatTimestamp(db.lastSync)
                            )
                        } catch (e: Exception) {
                            _uiState.value = KardexUiState.Error("Error al procesar el Kardex.")
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun extractSummaryData(json: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        try {
            val root = if (json.startsWith("{")) JSONObject(json) else JSONObject()
            
            // Buscar las llaves del resumen en cualquier parte del objeto JSON
            fun findKey(obj: JSONObject, targetKey: String): String? {
                if (obj.has(targetKey)) return obj.optString(targetKey)
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val child = obj.opt(key)
                    if (child is JSONObject) {
                        val found = findKey(child, targetKey)
                        if (found != null) return found
                    } else if (child is JSONArray && child.length() > 0) {
                        val first = child.optJSONObject(0)
                        if (first != null) {
                            val found = findKey(first, targetKey)
                            if (found != null) return found
                        }
                    }
                }
                return null
            }

            result["promedio"] = findKey(root, "PromedioGral") ?: findKey(root, "promedio") ?: "0.0"
            result["aprobadas"] = findKey(root, "MatAprobadas") ?: findKey(root, "aprobadas") ?: "0"
            result["totales"] = findKey(root, "MatCursadas") ?: findKey(root, "totales") ?: "0"
            result["creditos"] = findKey(root, "CdtsAcum") ?: findKey(root, "creditos") ?: "0"
            
        } catch (e: Exception) { }
        return result
    }

    private fun parseKardexAggressively(json: String): List<Map<String, String>> {
        val result = mutableListOf<Map<String, String>>()
        try {
            if (json.startsWith("[")) {
                val array = JSONArray(json)
                for (i in 0 until array.length()) result.add(jsonObjectToMap(array.getJSONObject(i)))
            } else if (json.startsWith("{")) {
                val obj = JSONObject(json)
                val array = obj.optJSONArray("lstKardex") ?: obj.optJSONArray("kardex")
                if (array != null) {
                    for (i in 0 until array.length()) result.add(jsonObjectToMap(array.getJSONObject(i)))
                } else {
                    val keys = obj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val value = obj.opt(key)
                        if (value is JSONArray && value.length() > 1) {
                            val first = value.optJSONObject(0)
                            if (first != null && (first.has("Materia") || first.has("materia"))) {
                                for (i in 0 until value.length()) result.add(jsonObjectToMap(value.getJSONObject(i)))
                                break
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) { }
        return result
    }

    private fun jsonObjectToMap(obj: JSONObject): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = obj.optString(key)
            map[key] = value
            map[key.lowercase()] = value
        }
        return map
    }

    fun load() {
        viewModelScope.launch {
            if (_uiState.value !is KardexUiState.Success) _uiState.value = KardexUiState.Loading
            if (NetworkUtils.isOnline(context)) SyncManager.enqueueFunctionalitySync(context, FetchFunctionalityWorker.FUNC_KARDEX)
        }
    }
}