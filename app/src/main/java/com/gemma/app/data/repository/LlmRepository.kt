package com.gemma.app.data.repository

import android.util.Log
import com.gemma.app.data.remote.SseResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

class LlmRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter(SseResponse::class.java)

    fun streamChat(
        message: String,
        onToken: (String) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ): EventSource {

        val jsonRequest = """
            {
              "model": "llama3",
              "messages": [{"role": "user", "content": "$message"}],
              "stream": true
            }
        """.trimIndent()

        val requestBody = jsonRequest.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://192.168.1.30:8080/api/chat")
            .header("Accept", "text/event-stream")
            .post(requestBody)
            .build()

        Log.d("LlmRepository", "Initializing SSE connection to ${request.url}")

        val eventSourceListener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.d("LlmRepository", "SSE Connection Opened successfully. Response code: ${response.code}")
            }

            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                if (data == "[DONE]") {
                    Log.d("LlmRepository", "Received [DONE] signal from SSE stream")
                    return
                }
                try {
                    val response = adapter.fromJson(data)
                    val content = response?.choices?.firstOrNull()?.delta?.content
                    if (content != null) {
                        onToken(content)
                    }
                } catch (e: Exception) {
                    Log.e("LlmRepository", "Error parsing SSE data: $data", e)
                }
            }

            override fun onClosed(eventSource: EventSource) {
                Log.d("LlmRepository", "SSE Connection Closed")
                onComplete()
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                Log.e("LlmRepository", "SSE Connection Failed! Response code: ${response?.code}", t)
                t?.let { onError(it) }
            }
        }

        Log.d("LlmRepository", "Sending request...")
        return EventSources.createFactory(client).newEventSource(request, eventSourceListener)
    }
}
