package com.rin.android.ui.screen.login

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rin.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) onLoginSuccess()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.login_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
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
            if (state.error != null) {
                Text(
                    text = stringResource(
                        when (state.error) {
                            "login_error_empty" -> R.string.login_error_empty
                            "login_error_invalid" -> R.string.login_error_invalid
                            "login_error_network" -> R.string.login_error_network
                            else -> R.string.login_error_failed
                        }
                    ),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (state.authStatus.second) {
                OutlinedTextField(
                    value = state.username,
                    onValueChange = viewModel::onUsernameChange,
                    label = { Text(stringResource(R.string.login_username)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text(stringResource(R.string.login_password)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { viewModel.login(onLoginSuccess) }),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                            )
                        }
                    },
                    enabled = !state.isLoading,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.login(onLoginSuccess) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(stringResource(R.string.login_button))
                }
            }

            if (state.authStatus.first && state.authStatus.second) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(stringResource(R.string.login_or), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (state.authStatus.first) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        val url = "${state.baseUrl.trimEnd('/')}/api/user/github"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.login_github))
                }
            }

            if (!state.authStatus.first && !state.authStatus.second) {
                Text(
                    text = stringResource(R.string.login_no_methods),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
