package com.rin.android.ui.screen.setup

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
class SetupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    data class UiState(
        val url: String = "",
        val isValidating: Boolean = false,
        val error: String? = null,
        val authStatus: Pair<Boolean, Boolean>? = null,
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        if (userPreferences.hasBaseUrlSync() && userPreferences.isLoggedInSync()) {
            _state.value = _state.value.copy(authStatus = Pair(false, false))
        }
    }

    fun onUrlChange(url: String) {
        _state.value = _state.value.copy(url = url, error = null)
    }

    fun validate(onSuccess: () -> Unit) {
        val url = _state.value.url.trim()
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            _state.value = _state.value.copy(error = "setup_error_invalid_url")
            return
        }
        _state.value = _state.value.copy(isValidating = true, error = null)
        viewModelScope.launch {
            authRepository.getAuthStatus(url)
                .onSuccess {
                    userPreferences.saveBaseUrl(url)
                    _state.value = _state.value.copy(isValidating = false, authStatus = Pair(it.github, it.password))
                    onSuccess()
                }
                .onFailure {
                    _state.value = _state.value.copy(isValidating = false, error = "setup_error_connection")
                }
        }
    }

    fun shouldSkipToHome(): Boolean = userPreferences.hasBaseUrlSync() && userPreferences.isLoggedInSync()
}
