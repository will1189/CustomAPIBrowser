package com.example.customapibrowser.data

import java.util.UUID

data class ApiConfig(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val url: String,
    val method: String = "GET",
    val headersJson: String = "{}",
    val bodyJson: String = "{}",
    val automationScript: String = ""
)
