package com.rin.android.ui.screen.drafts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rin.android.R
import com.rin.android.data.local.entity.DraftEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftsScreen(
    onBack: () -> Unit,
    onEditDraft: (Long) -> Unit,
    viewModel: DraftsViewModel = hiltViewModel(),
) {
    val drafts by viewModel.drafts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.drafts_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        if (drafts.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.drafts_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(Modifier.padding(padding).padding(horizontal = 16.dp)) {
                items(drafts, key = { it.id }) { draft ->
                    DraftItem(
                        draft = draft,
                        onDelete = { viewModel.deleteDraft(draft.id) },
                        onClick = { onEditDraft(draft.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DraftItem(draft: DraftEntity, onDelete: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    draft.title.ifBlank { "Untitled" },
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (draft.content.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        draft.content.take(100),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(draft.updatedAt, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.detail_delete), tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
