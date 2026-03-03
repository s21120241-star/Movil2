package com.example.movil2.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movil2.data.local.entity.AlumnoDB
import com.example.movil2.data.repository.Isicenetlocalrepository
import com.example.movil2.data.repository.ISicenetRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed interface ProfileUiState {
    data class Success(val json: JSONObject, val lastSync: Long = 0L) : ProfileUiState
    object Loading : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(
    private val remoteRepository: ISicenetRepository,
    private val localRepository: Isicenetlocalrepository
) : ViewModel() {

    var uiState: ProfileUiState by mutableStateOf(ProfileUiState.Loading)
        private set

    fun loadProfile() {
        viewModelScope.launch {
            uiState = ProfileUiState.Loading
            try {
                // Intentar cargar desde la base de datos local primero
                val localAlumno: AlumnoDB? = localRepository.getAlumno()
                if (localAlumno != null) {
                    uiState = ProfileUiState.Success(alumnoToJson(localAlumno), localAlumno.lastSync)
                } else {
                    // Fallback a remoto
                    val result = remoteRepository.getProfile()
                    if (result != null) {
                        uiState = ProfileUiState.Success(JSONObject(result))
                    } else {
                        uiState = ProfileUiState.Error("No se pudo obtener el perfil académico")
                    }
                }
            } catch (e: Exception) {
                uiState = ProfileUiState.Error("Error: ${e.message}")
            }
        }
    }

    private fun alumnoToJson(a: AlumnoDB): JSONObject = JSONObject().apply {
        put("nombre", a.nombre)
        put("matricula", a.matricula)
        put("carrera", a.carrera)
        put("especialidad", a.especialidad)
        put("semActual", a.semActual)
        put("cdtosAcumulados", a.cdtosAcumulados)
        put("cdtosActuales", a.cdtosActuales)
        put("estatus", a.estatus)
        put("fechaReins", a.fechaReins)
        put("urlFoto", a.urlFoto)
    }
}