package com.rin.android.ui.screen.setup

import android.content.res.Resources
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rin.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onNext: () -> Unit,
    onAlreadyLoggedIn: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if (viewModel.shouldSkipToHome()) {
            onAlreadyLoggedIn()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.setup_title)) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            OutlinedTextField(
                value = state.url,
                onValueChange = viewModel::onUrlChange,
                label = { Text(stringResource(R.string.setup_hint)) },
                placeholder = { Text("https://yourblog.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { viewModel.validate(onNext) }),
                isError = state.error != null,
                supportingText = state.error?.let { { Text(stringResource(R.string.setup_error_connection)) } },
                enabled = !state.isValidating,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.validate(onNext) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isValidating && state.url.isNotBlank(),
            ) {
                if (state.isValidating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.setup_validating))
                } else {
                    Text(stringResource(R.string.setup_connect))
                }
            }
        }
    }
}
