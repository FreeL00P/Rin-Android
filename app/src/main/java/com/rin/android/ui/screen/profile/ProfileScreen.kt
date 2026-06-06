package com.rin.android.ui.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.rin.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var uploadExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.loggedOut) { if (state.loggedOut) onLogout() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (state.profile != null && !state.isEditing) {
                        IconButton(onClick = viewModel::startEditing) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.detail_edit))
                        }
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.profile != null -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val profile = state.profile!!
                    if (profile.avatar.isNullOrBlank().not()) {
                        AsyncImage(
                            model = profile.avatar,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(MaterialTheme.shapes.large),
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    if (state.isEditing) {
                        OutlinedTextField(
                            value = state.editUsername,
                            onValueChange = viewModel::onUsernameChange,
                            label = { Text(stringResource(R.string.profile_username)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.editAvatar,
                            onValueChange = viewModel::onAvatarChange,
                            label = { Text(stringResource(R.string.profile_avatar)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = viewModel::cancelEditing, modifier = Modifier.weight(1f)) {
                                Text(stringResource(R.string.detail_cancel))
                            }
                            Button(onClick = viewModel::saveProfile, modifier = Modifier.weight(1f), enabled = !state.isSaving) {
                                Text(stringResource(R.string.profile_save))
                            }
                        }
                    } else {
                        Text(profile.username, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(24.dp))

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        stringResource(R.string.upload_settings),
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                    IconButton(onClick = { uploadExpanded = !uploadExpanded }) {
                                        Icon(
                                            if (uploadExpanded) Icons.Default.KeyboardArrowUp
                                            else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                        )
                                    }
                                }
                                if (uploadExpanded) {
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        stringResource(R.string.upload_mode_label),
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilterChip(
                                            selected = state.uploadMode == "rin",
                                            onClick = { viewModel.onUploadModeChange("rin") },
                                            label = { Text(stringResource(R.string.upload_mode_rin)) },
                                        )
                                        FilterChip(
                                            selected = state.uploadMode == "external",
                                            onClick = { viewModel.onUploadModeChange("external") },
                                            label = { Text(stringResource(R.string.upload_mode_external)) },
                                        )
                                    }

                                    if (state.uploadMode == "external") {
                                        Spacer(Modifier.height(12.dp))
                                        OutlinedTextField(
                                            value = state.uploadUrl,
                                            onValueChange = viewModel::onUploadUrlChange,
                                            label = { Text(stringResource(R.string.upload_url)) },
                                            placeholder = { Text("https://tu.example.com/upload") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = state.uploadUser,
                                            onValueChange = viewModel::onUploadUserChange,
                                            label = { Text(stringResource(R.string.upload_user)) },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = state.uploadPass,
                                            onValueChange = viewModel::onUploadPassChange,
                                            label = { Text(stringResource(R.string.upload_pass)) },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            visualTransformation = PasswordVisualTransformation(),
                                        )
                                        Spacer(Modifier.height(12.dp))
                                        Button(
                                            onClick = viewModel::saveUploadConfig,
                                            modifier = Modifier.fillMaxWidth(),
                                            enabled = !state.uploadSaved,
                                        ) {
                                            Text(if (state.uploadSaved) stringResource(R.string.profile_saved) else stringResource(R.string.profile_save))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        OutlinedButton(
                            onClick = viewModel::showLogoutDialog,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        ) {
                            Text(stringResource(R.string.profile_logout))
                        }
                    }
                }
            }
        }

        if (state.showLogoutDialog) {
            AlertDialog(
                onDismissRequest = viewModel::hideLogoutDialog,
                title = { Text(stringResource(R.string.profile_logout)) },
                text = { Text(stringResource(R.string.profile_logout_confirm)) },
                confirmButton = {
                    TextButton(onClick = viewModel::logout) {
                        Text(stringResource(R.string.detail_confirm), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::hideLogoutDialog) {
                        Text(stringResource(R.string.detail_cancel))
                    }
                },
            )
        }
    }
}
