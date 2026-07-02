package com.example.customapibrowser.api

import com.example.customapibrowser.data.ApiConfig
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiEngine {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()

    suspend fun execute(config: ApiConfig): Result<String> = withContext(Dispatchers.IO) {
        try {
            val requestBuilder = Request.Builder().url(config.url)

            val headers = parseHeaders(config.headersJson)
            headers.forEach { (key, value) ->
                requestBuilder.addHeader(key, value)
            }

            val method = config.method.uppercase()
            val body = when (method) {
                "GET", "DELETE" -> null
                else -> {
                    val mediaType = headers["Content-Type"]
                        ?: "application/json; charset=utf-8"
                    config.bodyJson.toRequestBody(mediaType.toMediaType())
                }
            }

            requestBuilder.method(method, body)

            val response = client.newCall(requestBuilder.build()).execute()
            val bodyString = response.body?.string() ?: ""
            if (response.isSuccessful) {
                Result.success(bodyString)
            } else {
                Result.failure(IOException("HTTP ${response.code}: $bodyString"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseHeaders(json: String): Map<String, String> {
        return try {
            val obj = gson.fromJson(json, JsonObject::class.java) ?: return emptyMap()
            obj.entrySet().associate { it.key to it.value.asString }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
