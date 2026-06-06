package com.rin.android.ui.screen.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rin.android.data.local.entity.DraftEntity
import com.rin.android.data.remote.dto.CreateFeedRequestDto
import com.rin.android.data.remote.dto.UpdateFeedRequestDto
import com.rin.android.domain.repository.DraftRepository
import com.rin.android.domain.repository.FeedRepository
import com.rin.android.domain.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository,
    private val draftRepository: DraftRepository,
    private val storageRepository: StorageRepository,
) : ViewModel() {

    data class UiState(
        val feedId: Int? = null,
        val title: String = "",
        val content: String = "",
        val summary: String = "",
        val tags: String = "",
        val alias: String = "",
        val isDraft: Boolean = false,
        val isListed: Boolean = true,
        val isPreview: Boolean = false,
        val isPublishing: Boolean = false,
        val isUploading: Boolean = false,
        val message: String? = null,
        val published: Boolean = false,
        val isLoadingFeed: Boolean = false,
        val localDraftId: Long? = null,
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        val id = savedStateHandle.get<Int>("id")?.takeIf { it > 0 }
        if (id != null) {
            _state.value = _state.value.copy(feedId = id, isLoadingFeed = true)
            loadFeed(id)
        }
    }

    private fun loadFeed(id: Int) {
        viewModelScope.launch {
            feedRepository.getFeed(id.toString()).onSuccess { feed ->
                _state.value = _state.value.copy(
                    title = feed.title ?: "",
                    content = feed.content,
                    summary = feed.summary,
                    tags = feed.hashtags.joinToString(", ") { it.name },
                    alias = feed.alias ?: "",
                    isDraft = feed.draft == 1,
                    isListed = feed.listed == 1,
                    isLoadingFeed = false,
                )
            }.onFailure {
                _state.value = _state.value.copy(isLoadingFeed = false, message = it.message)
            }
        }
    }

    fun onTitleChange(v: String) { _state.value = _state.value.copy(title = v) }
    fun onContentChange(v: String) { _state.value = _state.value.copy(content = v) }
    fun onSummaryChange(v: String) { _state.value = _state.value.copy(summary = v) }
    fun onTagsChange(v: String) { _state.value = _state.value.copy(tags = v) }
    fun onAliasChange(v: String) { _state.value = _state.value.copy(alias = v) }
    fun onDraftChange(v: Boolean) { _state.value = _state.value.copy(isDraft = v) }
    fun onListedChange(v: Boolean) { _state.value = _state.value.copy(isListed = v) }
    fun togglePreview() { _state.value = _state.value.copy(isPreview = !_state.value.isPreview) }

    fun insertAtCursor(before: String, after: String = "", selectedText: String = "") {
        val s = _state.value
        val newContent = s.content + before + selectedText + after
        _state.value = s.copy(content = newContent)
    }

    fun publish() {
        val s = _state.value
        if (s.title.isBlank()) {
            _state.value = s.copy(message = "Title is required")
            return
        }
        _state.value = s.copy(isPublishing = true, message = null)
        val tagsList = s.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        viewModelScope.launch {
            if (s.feedId != null) {
                feedRepository.updateFeed(
                    s.feedId,
                    UpdateFeedRequestDto(
                        title = s.title,
                        content = s.content,
                        summary = s.summary.ifBlank { null },
                        alias = s.alias.ifBlank { null },
                        listed = s.isListed,
                        draft = s.isDraft,
                        tags = tagsList,
                    ),
                ).onSuccess {
                    _state.value = _state.value.copy(isPublishing = false, published = true)
                }.onFailure {
                    _state.value = _state.value.copy(isPublishing = false, message = "Publish failed: ${it.message}")
                }
            } else {
                feedRepository.createFeed(
                    CreateFeedRequestDto(
                        title = s.title,
                        content = s.content,
                        listed = s.isListed,
                        draft = s.isDraft,
                        tags = tagsList,
                    ),
                ).onSuccess {
                    _state.value = _state.value.copy(isPublishing = false, published = true)
                }.onFailure {
                    _state.value = _state.value.copy(isPublishing = false, message = "Publish failed: ${it.message}")
                }
            }
        }
    }

    fun saveDraft() {
        val s = _state.value
        viewModelScope.launch {
            val draft = DraftEntity(
                id = s.localDraftId ?: 0,
                title = s.title,
                content = s.content,
                summary = s.summary,
                tags = s.tags,
                alias = s.alias,
                listed = s.isListed,
                draft = true,
                remoteId = s.feedId,
                updatedAt = System.currentTimeMillis().toString(),
            )
            val id = draftRepository.saveDraft(draft)
            _state.value = _state.value.copy(localDraftId = id, message = "Draft saved")
        }
    }

    fun uploadImage(fileName: String, byteArray: ByteArray) {
        val s = _state.value
        _state.value = s.copy(isUploading = true)
        viewModelScope.launch {
            storageRepository.uploadImage(fileName, byteArray)
                .onSuccess { url ->
                    val imageMd = "![$fileName]($url)"
                    _state.value = _state.value.copy(
                        content = s.content + "\n$imageMd\n",
                        isUploading = false,
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(isUploading = false, message = "image_error: ${it.message}")
                }
        }
    }

    fun clearMessage() { _state.value = _state.value.copy(message = null) }
}
