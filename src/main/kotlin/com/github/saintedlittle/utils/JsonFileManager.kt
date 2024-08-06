package com.github.saintedlittle.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object JsonFileManager {

    private val json = Json { ignoreUnknownKeys = true }

    fun readJsonFile(file: File): Map<String, Any> {
        val content = file.readText()
        return json.decodeFromString(content)
    }

    fun writeJsonFile(file: File, data: Map<String, Any>) {
        val jsonString = json.encodeToString(data)
        file.writeText(jsonString)
    }
}