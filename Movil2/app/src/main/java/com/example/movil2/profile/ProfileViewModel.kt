package com.example.movil2.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movil2.data.repository.SicenetRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed interface ProfileUiState {
    data class Success(val json: JSONObject) : ProfileUiState
    object Loading : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(private val repository: SicenetRepository) : ViewModel() {

    var uiState: ProfileUiState by mutableStateOf(ProfileUiState.Loading)
        private set

    fun loadProfile() {
        viewModelScope.launch {
            uiState = ProfileUiState.Loading
            println("Loading")
            try {
                val result = repository.getProfile()
                if (result != null) {
                    val json = JSONObject(result)
                    uiState = ProfileUiState.Success(json)
                    println("Success")
                } else {
                    uiState = ProfileUiState.Error("No se pudo obtener el perfil académico")
                    println("Error (sin datos)")
                }
            } catch (e: Exception) {
                uiState = ProfileUiState.Error("Error: ${e.message}")
                println("Error (${e.message})")
            }
        }
    }

}
