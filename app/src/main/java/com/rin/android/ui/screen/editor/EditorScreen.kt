package com.rin.android.ui.screen.editor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rin.android.R
import com.rin.android.ui.component.MarkdownContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    feedId: Int?,
    onBack: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = androidx.compose.ui.platform.LocalContext.current
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@rememberLauncherForActivityResult
            val bytes = inputStream.readBytes()
            inputStream.close()
            val fileName = uri.lastPathSegment ?: "image.jpg"
            viewModel.uploadImage(fileName, bytes)
        } catch (_: Exception) {}
    }

    LaunchedEffect(state.published) {
        if (state.published) onBack()
    }

    LaunchedEffect(state.message) {
        val msg = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearMessage()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    val label = if (state.isPreview) stringResource(R.string.editor_preview) else stringResource(R.string.editor_edit)
                    Text(label)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::togglePreview) {
                        Icon(
                            if (state.isPreview) Icons.Default.Edit else Icons.Default.Visibility,
                            contentDescription = if (state.isPreview) stringResource(R.string.editor_edit) else stringResource(R.string.editor_preview),
                        )
                    }
                    if (!state.isPreview) {
                        IconButton(onClick = { imageLauncher.launch("image/*") }) {
                            Icon(Icons.Default.Image, contentDescription = stringResource(R.string.toolbar_image))
                        }
                    }
                    val publishText = if (state.feedId != null) stringResource(R.string.editor_update) else stringResource(R.string.editor_publish)
                    TextButton(
                        onClick = viewModel::publish,
                        enabled = !state.isPublishing,
                    ) {
                        if (state.isPublishing) {
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(4.dp))
                        }
                        Text(publishText)
                    }
                },
            )
        },
    ) { padding ->
        if (state.isLoadingFeed) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.isPreview) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                Text(state.title, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))
                MarkdownContent(markdown = state.content)
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding)) {
                MarkdownToolbar(viewModel)
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChange,
                    placeholder = { Text(stringResource(R.string.editor_title_hint)) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.headlineSmall,
                )
                OutlinedTextField(
                    value = state.content,
                    onValueChange = viewModel::onContentChange,
                    placeholder = { Text(stringResource(R.string.editor_content_hint)) },
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                )
                EditorBottomSheet(viewModel, state)
            }
        }
    }
}

@Composable
private fun MarkdownToolbar(viewModel: com.rin.android.ui.screen.editor.EditorViewModel) {
    Row(
        Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        listOf("# ", "## ", "### ").forEach { prefix ->
            TextButton(onClick = { viewModel.insertAtCursor(prefix) }) {
                Text(prefix.trim(), style = MaterialTheme.typography.labelMedium)
            }
        }
        TextButton(onClick = { viewModel.insertAtCursor("**", "**") }) {
            Text("B", style = MaterialTheme.typography.labelMedium)
        }
        TextButton(onClick = { viewModel.insertAtCursor("*", "*") }) {
            Text("I", style = MaterialTheme.typography.labelMedium)
        }
        TextButton(onClick = { viewModel.insertAtCursor("`", "`") }) {
            Text("</>", style = MaterialTheme.typography.labelMedium)
        }
        TextButton(onClick = { viewModel.insertAtCursor("[", "](url)") }) {
            Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp))
        }
        TextButton(onClick = { viewModel.insertAtCursor("> ") }) {
            Icon(Icons.Default.FormatQuote, contentDescription = null, modifier = Modifier.size(18.dp))
        }
        TextButton(onClick = { viewModel.insertAtCursor("- ") }) {
            Icon(Icons.Default.FormatListBulleted, contentDescription = null, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(8.dp))
        TextButton(onClick = viewModel::saveDraft) {
            Text(stringResource(R.string.editor_save_draft), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun EditorBottomSheet(viewModel: com.rin.android.ui.screen.editor.EditorViewModel, state: com.rin.android.ui.screen.editor.EditorViewModel.UiState) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.editor_visibility), style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = state.isListed,
                onClick = { viewModel.onListedChange(true) },
                label = { Text(stringResource(R.string.editor_listed), style = MaterialTheme.typography.labelSmall) },
            )
            Spacer(Modifier.width(4.dp))
            FilterChip(
                selected = !state.isListed,
                onClick = { viewModel.onListedChange(false) },
                label = { Text(stringResource(R.string.editor_unlisted), style = MaterialTheme.typography.labelSmall) },
            )
        }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.Settings, contentDescription = null)
        }
    }
    if (expanded) {
        Column(Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)) {
            OutlinedTextField(
                value = state.summary,
                onValueChange = viewModel::onSummaryChange,
                label = { Text(stringResource(R.string.editor_summary)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = state.tags,
                onValueChange = viewModel::onTagsChange,
                label = { Text(stringResource(R.string.editor_tags)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = state.alias,
                onValueChange = viewModel::onAliasChange,
                label = { Text(stringResource(R.string.editor_alias)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.editor_draft_label), style = MaterialTheme.typography.labelMedium)
                Switch(checked = state.isDraft, onCheckedChange = viewModel::onDraftChange)
            }
        }
    }
}
