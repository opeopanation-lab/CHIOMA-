package com.example.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class PingResult(
    val isSuccess: Boolean,
    val message: String,
    val agent: String = ""
)

data class ExecutionResult(
    val isSuccess: Boolean,
    val output: String,
    val exitCode: Int,
    val chiomaNote: String = ""
)

object McpClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    suspend fun ping(baseUrl: String): PingResult = withContext(Dispatchers.IO) {
        val cleanUrl = formatBaseUrl(baseUrl)
        val request = Request.Builder()
            .url("$cleanUrl/ping")
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val json = JSONObject(body)
                    val agent = json.optString("agent", "Chioma")
                    val msg = json.optString("message", "Unlimited. Free. Ready.")
                    PingResult(true, msg, agent)
                } else {
                    PingResult(false, "Server active, but returned status code: ${response.code}")
                }
            }
        } catch (e: Exception) {
            PingResult(false, e.localizedMessage ?: "Connection timed out.")
        }
    }

    suspend fun executeCommand(baseUrl: String, command: String): ExecutionResult = withContext(Dispatchers.IO) {
        val cleanUrl = formatBaseUrl(baseUrl)
        val payload = JSONObject().apply {
            put("command", command)
        }

        val requestBody = payload.toString().toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$cleanUrl/run")
            .post(requestBody)
            .addHeader("X-Chioma", "Free-Forever")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val json = JSONObject(body)
                    val output = json.optString("output", "Command executed successfully.")
                    // Handle server both sending 'status' or 'exitCode'
                    val isOk = json.optString("status", "success") == "success" || json.optInt("exitCode", 0) == 0
                    val chiomaNote = json.optString("chiomaNote", "Nnọọ! Command executed freely 😊")
                    
                    ExecutionResult(isOk, output, if (isOk) 0 else 1, chiomaNote)
                } else {
                    ExecutionResult(
                        isSuccess = false,
                        output = "HTTP Error: ${response.code}\nResponse: $body",
                        exitCode = response.code,
                        chiomaNote = "Connection fault. Check Termux terminal status!"
                    )
                }
            }
        } catch (e: Exception) {
            ExecutionResult(
                isSuccess = false,
                output = e.localizedMessage ?: "Failed to connect to Termux MCP bridge.",
                exitCode = -1,
                chiomaNote = "Could not reach Termux! Is the chioma-mcp script running?"
            )
        }
    }

    private fun formatBaseUrl(url: String): String {
        var formatted = url.trim()
        if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
            formatted = "http://$formatted"
        }
        if (formatted.endsWith("/")) {
            formatted = formatted.substring(0, formatted.length - 1)
        }
        return formatted
    }
}
