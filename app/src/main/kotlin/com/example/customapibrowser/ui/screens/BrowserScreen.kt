package com.example.customapibrowser.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.customapibrowser.R
import com.example.customapibrowser.browser.BrowserViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(viewModel: BrowserViewModel) {
    val context = LocalContext.current
    val automationResult by viewModel.automationResult.collectAsState()

    var urlText by remember { mutableStateOf("https://www.example.com") }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var scriptText by remember {
        mutableStateOf(
            """// 在页面上下文中执行 JavaScript
// 示例：点击第一个按钮
document.querySelector('button')?.click();
"""
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = urlText,
                onValueChange = { urlText = it },
                label = { Text(stringResource(R.string.url)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(
                onClick = { webView?.loadUrl(urlText) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(stringResource(R.string.go))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { webView?.goBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
            IconButton(onClick = { webView?.goForward() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(R.string.forward))
            }
            IconButton(onClick = { webView?.reload() }) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.reload))
            }
            if (isLoading) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                )
            }
        }

        AndroidView(
            factory = {
                WebView(context).apply {
                    viewModel.configureWebView(this)
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            progress = newProgress / 100f
                            isLoading = newProgress in 1..99
                        }
                    }
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            url?.let { urlText = it }
                            isLoading = false
                        }
                    }
                    loadUrl(urlText)
                    webView = this
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(220.dp)
        ) {
            Text(
                text = stringResource(R.string.automation_script),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = scriptText,
                onValueChange = { scriptText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                maxLines = 8
            )
            Button(
                onClick = { webView?.let { viewModel.runAutomation(it, scriptText) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Text(stringResource(R.string.run_automation))
            }
        }
    }

    automationResult?.let { result ->
        AlertDialog(
            onDismissRequest = { viewModel.clearAutomationResult() },
            title = { Text("自动化结果") },
            text = { Text(result) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearAutomationResult() }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}
