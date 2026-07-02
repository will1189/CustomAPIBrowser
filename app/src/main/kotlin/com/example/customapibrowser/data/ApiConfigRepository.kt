package com.example.customapibrowser.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ApiConfigRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("api_configs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getAll(): List<ApiConfig> {
        val json = prefs.getString(KEY_APIS, "[]") ?: "[]"
        val type = object : TypeToken<List<ApiConfig>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun save(config: ApiConfig) {
        val current = getAll().toMutableList()
        val index = current.indexOfFirst { it.id == config.id }
        if (index >= 0) {
            current[index] = config
        } else {
            current.add(config)
        }
        persist(current)
    }

    fun delete(id: String) {
        val current = getAll().filter { it.id != id }
        persist(current)
    }

    private fun persist(list: List<ApiConfig>) {
        prefs.edit().putString(KEY_APIS, gson.toJson(list)).apply()
    }

    companion object {
        private const val KEY_APIS = "apis"
    }
}
