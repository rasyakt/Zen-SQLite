package com.example.zensqlite.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zensqlite.ZenApplication
import com.example.zensqlite.data.entity.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val registerSuccess: Boolean = false,
    val errorMessage: String? = null,
    val currentUser: UserEntity? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = (application as ZenApplication).userRepository
    private val prefs = application.getSharedPreferences("zen_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkLoginSession()
    }

    private fun checkLoginSession() {
        val userId = prefs.getLong("user_id", -1L)
        if (userId != -1L) {
            viewModelScope.launch {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    _uiState.value = _uiState.value.copy(
                        currentUser = user,
                        loginSuccess = true
                    )
                }
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Semua field harus diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = userRepository.loginUser(email.trim(), password)
            result.onSuccess { user ->
                prefs.edit().putLong("user_id", user.id).apply()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loginSuccess = true,
                    currentUser = user,
                    errorMessage = null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Login gagal"
                )
            }
        }
    }

    fun register(fullName: String, email: String, password: String, confirmPassword: String) {
        when {
            fullName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Semua field harus diisi")
                return
            }
            fullName.trim().length < 2 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Nama minimal 2 karakter")
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Format email tidak valid")
                return
            }
            password.length < 6 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Password minimal 6 karakter")
                return
            }
            password != confirmPassword -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Konfirmasi password tidak cocok")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = userRepository.registerUser(fullName.trim(), email.trim(), password)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    registerSuccess = true,
                    errorMessage = null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Registrasi gagal"
                )
            }
        }
    }

    fun logout() {
        prefs.edit().remove("user_id").apply()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetRegisterSuccess() {
        _uiState.value = _uiState.value.copy(registerSuccess = false)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getLong("user_id", -1L) != -1L
    }
}
