package com.rin.android.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rin.android.data.local.UserPreferences
import com.rin.android.data.remote.dto.UpdateProfileRequestDto
import com.rin.android.data.remote.dto.UserDto
import com.rin.android.domain.repository.AuthRepository
import com.rin.android.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    data class UiState(
        val profile: UserDto? = null,
        val isLoading: Boolean = true,
        val isEditing: Boolean = false,
        val editUsername: String = "",
        val editAvatar: String = "",
        val isSaving: Boolean = false,
        val showLogoutDialog: Boolean = false,
        val loggedOut: Boolean = false,
        val uploadMode: String = "rin",
        val uploadUrl: String = "",
        val uploadUser: String = "",
        val uploadPass: String = "",
        val uploadSaved: Boolean = false,
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadProfile()
        loadUploadConfig()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            userRepository.getProfile()
                .onSuccess { user ->
                    _state.value = _state.value.copy(profile = user, isLoading = false, editUsername = user.username, editAvatar = user.avatar ?: "")
                }
                .onFailure { _state.value = _state.value.copy(isLoading = false) }
        }
    }

    private fun loadUploadConfig() {
        _state.value = _state.value.copy(
            uploadMode = userPreferences.getUploadModeSync(),
        )
        viewModelScope.launch {
            _state.value = _state.value.copy(
                uploadUrl = userPreferences.getUploadUrl() ?: "",
                uploadUser = userPreferences.getUploadUser() ?: "",
                uploadPass = userPreferences.getUploadPass() ?: "",
            )
        }
    }

    fun startEditing() { _state.value = _state.value.copy(isEditing = true) }
    fun cancelEditing() { _state.value = _state.value.copy(isEditing = false) }
    fun onUsernameChange(v: String) { _state.value = _state.value.copy(editUsername = v) }
    fun onAvatarChange(v: String) { _state.value = _state.value.copy(editAvatar = v) }

    fun saveProfile() {
        val s = _state.value
        _state.value = s.copy(isSaving = true)
        viewModelScope.launch {
            userRepository.updateProfile(UpdateProfileRequestDto(username = s.editUsername, avatar = s.editAvatar))
                .onSuccess { _state.value = _state.value.copy(isSaving = false, isEditing = false); loadProfile() }
                .onFailure { _state.value = _state.value.copy(isSaving = false) }
        }
    }

    fun onUploadModeChange(mode: String) {
        _state.value = _state.value.copy(uploadMode = mode, uploadSaved = false)
        viewModelScope.launch { userPreferences.saveUploadMode(mode) }
    }

    fun onUploadUrlChange(v: String) { _state.value = _state.value.copy(uploadUrl = v, uploadSaved = false) }
    fun onUploadUserChange(v: String) { _state.value = _state.value.copy(uploadUser = v, uploadSaved = false) }
    fun onUploadPassChange(v: String) { _state.value = _state.value.copy(uploadPass = v, uploadSaved = false) }

    fun saveUploadConfig() {
        val s = _state.value
        viewModelScope.launch {
            userPreferences.saveUploadUrl(s.uploadUrl)
            userPreferences.saveUploadUser(s.uploadUser)
            userPreferences.saveUploadPass(s.uploadPass)
            _state.value = _state.value.copy(uploadSaved = true)
        }
    }

    fun showLogoutDialog() { _state.value = _state.value.copy(showLogoutDialog = true) }
    fun hideLogoutDialog() { _state.value = _state.value.copy(showLogoutDialog = false) }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.value = _state.value.copy(showLogoutDialog = false, loggedOut = true)
        }
    }
}
