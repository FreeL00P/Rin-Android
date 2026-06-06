package com.rin.android.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rin.android.data.remote.dto.FeedListItemDto
import com.rin.android.data.remote.dto.FeedListResponseDto
import com.rin.android.domain.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
) : ViewModel() {

    data class UiState(
        val query: String = "",
        val results: List<FeedListItemDto> = emptyList(),
        val isSearching: Boolean = false,
        val searched: Boolean = false,
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun onQueryChange(v: String) { _state.value = _state.value.copy(query = v) }

    fun search() {
        val q = _state.value.query.trim()
        if (q.isBlank()) return
        _state.value = _state.value.copy(isSearching = true)
        viewModelScope.launch {
            feedRepository.search(q)
                .onSuccess { _state.value = _state.value.copy(results = it.data, isSearching = false, searched = true) }
                .onFailure { _state.value = _state.value.copy(isSearching = false, searched = true) }
        }
    }
}
