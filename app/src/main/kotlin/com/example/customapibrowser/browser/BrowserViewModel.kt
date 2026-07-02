package com.example.customapibrowser.browser

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customapibrowser.api.ApiEngine
import com.example.customapibrowser.data.ApiConfig
import com.example.customapibrowser.data.ApiConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BrowserViewModel(
    private val repository: ApiConfigRepository,
    private val apiEngine: ApiEngine = ApiEngine()
) : ViewModel() {

    private val _apis = MutableStateFlow<List<ApiConfig>>(emptyList())
    val apis: StateFlow<List<ApiConfig>> = _apis.asStateFlow()

    private val _executeResult = MutableStateFlow<String?>(null)
    val executeResult: StateFlow<String?> = _executeResult.asStateFlow()

    private val _automationResult = MutableStateFlow<String?>(null)
    val automationResult: StateFlow<String?> = _automationResult.asStateFlow()

    init {
        loadApis()
    }

    fun loadApis() {
        _apis.value = repository.getAll()
    }

    fun saveApi(config: ApiConfig) {
        repository.save(config)
        loadApis()
    }

    fun deleteApi(id: String) {
        repository.delete(id)
        loadApis()
    }

    fun getApiById(id: String?): ApiConfig? {
        if (id == null) return null
        return apis.value.find { it.id == id }
    }

    fun executeApi(config: ApiConfig) {
        viewModelScope.launch {
            val result = apiEngine.execute(config)
            _executeResult.value = result.getOrElse { it.message ?: "Unknown error" }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun configureWebView(webView: WebView) {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = false
    }

    fun runAutomation(webView: WebView, script: String) {
        viewModelScope.launch {
            val wrappedScript = """
                (function() {
                    try {
                        $script
                        return JSON.stringify({ success: true, message: 'Automation finished' });
                    } catch (e) {
                        return JSON.stringify({ success: false, error: e.message });
                    }
                })();
            """.trimIndent()
            webView.evaluateJavascript(wrappedScript) { raw ->
                val cleaned = raw?.removeSurrounding("\"")?.replace("\\\"", "\"") ?: raw
                _automationResult.value = cleaned
            }
        }
    }

    fun clearExecuteResult() {
        _executeResult.value = null
    }

    fun clearAutomationResult() {
        _automationResult.value = null
    }
}
