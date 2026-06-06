package com.rin.android.ui.screen.detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rin.android.R
import com.rin.android.ui.component.MarkdownContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedDetailScreen(
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    viewModel: FeedDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.deleted) { if (state.deleted) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.feed?.title ?: "", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (state.feed != null) {
                        IconButton(onClick = { onEdit(state.feed!!.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.detail_edit))
                        }
                        IconButton(onClick = viewModel::showDeleteDialog) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.detail_delete))
                        }
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text(state.error ?: "Error", color = MaterialTheme.colorScheme.error)
                }
            }
            state.feed != null -> {
                val feed = state.feed!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    Text(feed.title ?: "", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    Row {
                        Text(feed.createdAt.take(10), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (feed.hashtags.isNotEmpty()) {
                            Spacer(Modifier.width(8.dp))
                            Text(feed.hashtags.joinToString(" ") { "#${it.name}" }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    MarkdownContent(markdown = feed.content)
                }
            }
        }

        if (state.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = viewModel::hideDeleteDialog,
                title = { Text(stringResource(R.string.detail_delete)) },
                text = { Text(stringResource(R.string.detail_delete_confirm)) },
                confirmButton = {
                    TextButton(onClick = viewModel::deleteFeed) {
                        Text(stringResource(R.string.detail_confirm), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::hideDeleteDialog) {
                        Text(stringResource(R.string.detail_cancel))
                    }
                },
            )
        }
    }
}
