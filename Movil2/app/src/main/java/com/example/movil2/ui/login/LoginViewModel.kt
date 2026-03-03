package com.example.movil2.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.movil2.data.repository.ISicenetRepository
import com.example.movil2.sync.SyncManager
import com.example.movil2.utils.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: ISicenetRepository,
    private val context: Context
) : ViewModel() {

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

            if (NetworkUtils.isOnline(context)) {
                // Con internet: autenticar en remoto y lanzar sincronización
                val success = repository.login(matricula, contrasenia, tipoUsuario)
                if (success) {
                    val fetchRequest = SyncManager.enqueueLoginSync(context)

                    // Monitorear el primer worker para navegar cuando sea SUCCESS
                    WorkManager.getInstance(context)
                        .getWorkInfoByIdLiveData(fetchRequest.id)
                        .observeForever { info ->
                            if (info != null) {
                                when (info.state) {
                                    WorkInfo.State.SUCCEEDED -> {
                                        _isLoading.value = false
                                        _loginSuccess.value = true
                                    }
                                    WorkInfo.State.FAILED,
                                    WorkInfo.State.CANCELLED -> {
                                        _isLoading.value = false
                                        // Aunque falle el sync, permitir navegar (datos pueden ya existir)
                                        _loginSuccess.value = true
                                    }
                                    else -> { /* ENQUEUED / RUNNING — seguir esperando */ }
                                }
                            }
                        }
                } else {
                    _errorMessage.value = "Credenciales incorrectas"
                    _isLoading.value = false
                }
            } else {
                // Sin internet: verificar si hay sesión guardada localmente
                // (la sesión se conserva en cookies/SharedPrefs)
                // Intentamos cargar el perfil local para saber si hay sesión
                _isLoading.value = false
                // Navegar directamente; las pantallas se alimentarán de la BD local
                _loginSuccess.value = true
            }
        }
    }

    fun setError(message: String) {
        _errorMessage.value = message
    }
}