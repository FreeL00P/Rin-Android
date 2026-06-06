package com.rin.android.ui.screen.drafts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rin.android.data.local.entity.DraftEntity
import com.rin.android.domain.repository.DraftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DraftsViewModel @Inject constructor(
    private val draftRepository: DraftRepository,
) : ViewModel() {

    val drafts: StateFlow<List<DraftEntity>> = draftRepository.getAllDrafts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteDraft(id: Long) {
        viewModelScope.launch { draftRepository.deleteDraft(id) }
    }
}
