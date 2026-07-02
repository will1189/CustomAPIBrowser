package com.example.customapibrowser.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.customapibrowser.R
import com.example.customapibrowser.browser.BrowserViewModel
import com.example.customapibrowser.data.ApiConfig

@Composable
fun ApiListScreen(
    viewModel: BrowserViewModel,
    onEdit: (ApiConfig) -> Unit,
    onAdd: () -> Unit
) {
    val apis by viewModel.apis.collectAsState()
    val executeResult by viewModel.executeResult.collectAsState()
    var apiToDelete by remember { mutableStateOf<ApiConfig?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_api))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(apis, key = { it.id }) { api ->
                ApiItem(
                    api = api,
                    onEdit = { onEdit(api) },
                    onDelete = { apiToDelete = api },
                    onRun = { viewModel.executeApi(api) }
                )
            }
        }
    }

    apiToDelete?.let { api ->
        AlertDialog(
            onDismissRequest = { apiToDelete = null },
            title = { Text("删除确认") },
            text = { Text("确定要删除 \"${api.name}\" 吗？") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteApi(api.id)
                    apiToDelete = null
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { apiToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    executeResult?.let { result ->
        AlertDialog(
            onDismissRequest = { viewModel.clearExecuteResult() },
            title = { Text(stringResource(R.string.response)) },
            text = {
                Text(
                    text = result,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(onClick = { viewModel.clearExecuteResult() }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}

@Composable
private fun ApiItem(
    api: ApiConfig,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRun: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = api.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${api.method} ${api.url}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRun) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.run))
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete))
                }
            }
        }
    }
}
