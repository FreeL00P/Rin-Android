package com.rin.android.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rin.android.data.local.UserPreferences
import com.rin.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    data class UiState(
        val username: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val loginSuccess: Boolean = false,
        val authStatus: Pair<Boolean, Boolean> = Pair(false, false),
        val baseUrl: String = "",
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val url = userPreferences.getBaseUrl() ?: ""
            _state.value = _state.value.copy(baseUrl = url)
            authRepository.getAuthStatus(url).onSuccess {
                _state.value = _state.value.copy(authStatus = Pair(it.github, it.password))
            }
        }
    }

    fun onUsernameChange(v: String) { _state.value = _state.value.copy(username = v, error = null) }
    fun onPasswordChange(v: String) { _state.value = _state.value.copy(password = v, error = null) }

    fun login(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.username.isBlank() || s.password.isBlank()) {
            _state.value = s.copy(error = "login_error_empty")
            return
        }
        _state.value = s.copy(isLoading = true, error = null)
        viewModelScope.launch {
            authRepository.login(s.baseUrl, s.username, s.password)
                .onSuccess {
                    if (it.success) {
                        _state.value = _state.value.copy(isLoading = false, loginSuccess = true)
                        onSuccess()
                    } else {
                        _state.value = _state.value.copy(isLoading = false, error = "login_error_failed")
                    }
                }
                .onFailure {
                    _state.value = _state.value.copy(isLoading = false, error = "login_error_network")
                }
        }
    }
}
