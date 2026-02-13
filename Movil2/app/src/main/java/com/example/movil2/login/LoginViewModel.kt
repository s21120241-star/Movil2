package com.example.movil2.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movil2.data.repository.SicenetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: SicenetRepository) : ViewModel() {

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun login(matricula: String, contrasenia: String, tipoUsuario: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val success = repository.login(matricula, contrasenia, tipoUsuario)
            _loginSuccess.value = success
            if (!success) {
                _errorMessage.value = "Credenciales incorrectas o error de conexión"
            }
            _isLoading.value = false
        }
    }

    fun setError(message: String) {
        _errorMessage.value = message
    }
}
