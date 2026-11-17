package com.lokin.app.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokin.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    val isLoggedIn get() = authRepository.currentUser != null

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.loginWithEmail(email, password)
                .onSuccess {
                    android.util.Log.d("LOKIN_AUTH", "Login success")
                    _uiState.value = AuthUiState(isSuccess = true)
                }
                .onFailure {
                    android.util.Log.e("LOKIN_AUTH", "Login failure: ${it.javaClass.name} - ${it.message}")
                    _uiState.value = AuthUiState(error = mapAuthError(it))
                }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.registerWithEmail(email, password)
                .onSuccess { _uiState.value = AuthUiState(isSuccess = true) }
                .onFailure { _uiState.value = AuthUiState(error = mapAuthError(it)) }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.loginWithGoogle(idToken)
                .onSuccess { _uiState.value = AuthUiState(isSuccess = true) }
                .onFailure { _uiState.value = AuthUiState(error = mapAuthError(it)) }
        }
    }

    fun showError(message: String) {
        _uiState.value = AuthUiState(error = message)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    private fun mapAuthError(e: Throwable): String {
        val msg = e.message?.lowercase() ?: return "Đã xảy ra lỗi, thử lại sau"
        android.util.Log.e("LOKIN_AUTH", "Auth error lowercase: $msg")
        return when {
            "no user record"           in msg -> "Email chưa được đăng ký"
            "credential is incorrect"  in msg -> "Email hoặc mật khẩu không đúng"
            "password is invalid"      in msg -> "Email hoặc mật khẩu không đúng"
            "email address is already" in msg -> "Email này đã được sử dụng"
            "badly formatted"          in msg -> "Email không hợp lệ"
            "network error"            in msg -> "Lỗi kết nối mạng, kiểm tra lại"
            "too many requests"        in msg -> "Quá nhiều lần thử, vui lòng đợi"
            "weak password"            in msg -> "Mật khẩu quá yếu, cần ít nhất 6 ký tự"
            "invalid credential"       in msg -> "Email hoặc mật khẩu không đúng"
            else -> "Đã xảy ra lỗi: $msg"   // tạm thời hiện msg gốc để debug
        }
    }
}