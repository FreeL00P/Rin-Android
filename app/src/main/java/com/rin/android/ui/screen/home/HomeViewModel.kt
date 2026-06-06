package com.rin.android.ui.screen.home

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
class HomeViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
) : ViewModel() {

    data class UiState(
        val feeds: List<FeedListItemDto> = emptyList(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val page: Int = 1,
        val hasNext: Boolean = true,
        val selectedTab: Int = 0,
        val error: String? = null,
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    private val typeMap = arrayOf("normal", "draft", "unlisted")

    init { loadFeeds(refresh = true) }

    fun loadFeeds(refresh: Boolean = false) {
        val s = _state.value
        if (s.isLoading) return
        if (!refresh && !s.hasNext) return

        val page = if (refresh) 1 else s.page
        _state.value = s.copy(isLoading = true, isRefreshing = refresh, error = null)

        viewModelScope.launch {
            feedRepository.getFeeds(page = page, limit = 20, type = typeMap.getOrNull(s.selectedTab))
                .onSuccess { response ->
                    _state.value = _state.value.copy(
                        feeds = if (refresh) response.data else s.feeds + response.data,
                        page = page + 1,
                        hasNext = response.hasNext,
                        isLoading = false,
                        isRefreshing = false,
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(isLoading = false, isRefreshing = false, error = it.message)
                }
        }
    }

    fun onTabChange(index: Int) {
        _state.value = _state.value.copy(selectedTab = index, feeds = emptyList(), page = 1, hasNext = true)
        loadFeeds(refresh = true)
    }

    fun refresh() = loadFeeds(refresh = true)

    fun loadMore() = loadFeeds(refresh = false)
}
