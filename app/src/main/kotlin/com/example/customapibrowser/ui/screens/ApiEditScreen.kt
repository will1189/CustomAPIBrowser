package com.example.customapibrowser.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.customapibrowser.R
import com.example.customapibrowser.browser.BrowserViewModel
import com.example.customapibrowser.data.ApiConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiEditScreen(
    viewModel: BrowserViewModel,
    apiId: String?,
    onSave: (ApiConfig) -> Unit,
    onBack: () -> Unit
) {
    val api = viewModel.getApiById(apiId)
    var name by remember { mutableStateOf(api?.name ?: "") }
    var url by remember { mutableStateOf(api?.url ?: "https://api.example.com") }
    var method by remember { mutableStateOf(api?.method ?: "GET") }
    var headers by remember { mutableStateOf(api?.headersJson ?: "{}") }
    var body by remember { mutableStateOf(api?.bodyJson ?: "{}") }
    var script by remember { mutableStateOf(api?.automationScript ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (api == null) "添加 API" else "编辑 API") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.name)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text(stringResource(R.string.url)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = method,
                onValueChange = { method = it.uppercase() },
                label = { Text(stringResource(R.string.method)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = headers,
                onValueChange = { headers = it },
                label = { Text(stringResource(R.string.headers)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = { Text(stringResource(R.string.body)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            OutlinedTextField(
                value = script,
                onValueChange = { script = it },
                label = { Text(stringResource(R.string.automation_script)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 8
            )
            Button(
                onClick = {
                    onSave(
                        (api ?: ApiConfig()).copy(
                            name = name,
                            url = url,
                            method = method,
                            headersJson = headers.ifBlank { "{}" },
                            bodyJson = body.ifBlank { "{}" },
                            automationScript = script
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
