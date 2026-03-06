package com.example.movil2.ui.carga

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

sealed interface CargaUiState {
    object Loading : CargaUiState
    data class Success(
        val scheduleByDay: Map<String, List<MateriaSchedule>>,
        val lastSync: String
    ) : CargaUiState
    data class Error(val message: String) : CargaUiState
}

data class MateriaSchedule(
    val nombre: String,
    val hora: String,
    val aula: String,
    val grupo: String
)

class CargaAcademicaViewModel(private val context: Context) : ViewModel() {

    private val localRepo: Sicenetlocalrepository by lazy {
        Sicenetlocalrepository(Sicenetdatabase.getDatabase(context).sicenetDao())
    }

    private val _uiState = MutableStateFlow<CargaUiState>(CargaUiState.Loading)
    val uiState: StateFlow<CargaUiState> = _uiState

    init {
        observeLocalData()
    }

    private fun observeLocalData() {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val matricula = prefs.getString("matricula_activa", "") ?: ""
        
        if (matricula.isNotEmpty()) {
            localRepo.getCargaAcademicaFlow(matricula)
                .onEach { db ->
                    if (db != null) {
                        val schedule = parseAndGroupCarga(db.jsonData)
                        _uiState.value = CargaUiState.Success(schedule, DateUtils.formatTimestamp(db.lastSync))
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun parseAndGroupCarga(json: String): Map<String, List<MateriaSchedule>> {
        val days = listOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")
        val result = days.associateWith { mutableListOf<MateriaSchedule>() }.toMutableMap()

        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                
                // Mapeo robusto de nombres de materia
                val nombre = listOf("materia", "nombreMateria", "asignatura", "nombreAsignatura", "Materia", "NombreMateria", "Asignatura")
                    .firstNotNullOfOrNull { key -> obj.optString(key).takeIf { it.isNotBlank() && it != "null" } }
                    ?: "Materia Desconocida"

                val aula = obj.optString("aula").ifEmpty { obj.optString("Aula") }
                val grupo = obj.optString("grupo").ifEmpty { obj.optString("Grupo") }

                for (day in days) {
                    val hora = obj.optString(day).ifEmpty { obj.optString(day.uppercase()) }
                    if (hora.isNotBlank() && hora != "null" && hora.contains("-")) {
                        result[day]?.add(MateriaSchedule(nombre, hora, aula, grupo))
                    }
                }
            }

            // Ordenar cada día por hora de inicio
            result.forEach { (day, list) ->
                result[day] = list.sortedBy { it.hora.split("-").firstOrNull() ?: "23:59" }.toMutableList()
            }

        } catch (e: Exception) { e.printStackTrace() }

        return result
    }

    fun load() {
        viewModelScope.launch {
            if (_uiState.value !is CargaUiState.Success) {
                _uiState.value = CargaUiState.Loading
            }
            if (NetworkUtils.isOnline(context)) {
                SyncManager.enqueueFunctionalitySync(context, FetchFunctionalityWorker.FUNC_CARGA)
            }
        }
    }
}
