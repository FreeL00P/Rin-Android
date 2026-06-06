package com.rin.android.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rin.android.data.remote.dto.FeedDto
import com.rin.android.domain.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository,
) : ViewModel() {

    data class UiState(
        val feed: FeedDto? = null,
        val isLoading: Boolean = true,
        val error: String? = null,
        val showDeleteDialog: Boolean = false,
        val deleted: Boolean = false,
    )

    private val feedId: String = savedStateHandle["id"] ?: ""
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init { loadFeed() }

    private fun loadFeed() {
        viewModelScope.launch {
            feedRepository.getFeed(feedId)
                .onSuccess { _state.value = UiState(feed = it, isLoading = false) }
                .onFailure { _state.value = UiState(isLoading = false, error = it.message) }
        }
    }

    fun showDeleteDialog() { _state.value = _state.value.copy(showDeleteDialog = true) }
    fun hideDeleteDialog() { _state.value = _state.value.copy(showDeleteDialog = false) }

    fun deleteFeed() {
        val id = _state.value.feed?.id ?: return
        viewModelScope.launch {
            feedRepository.deleteFeed(id)
                .onSuccess { _state.value = _state.value.copy(showDeleteDialog = false, deleted = true) }
                .onFailure { _state.value = _state.value.copy(showDeleteDialog = false) }
        }
    }
}
