package com.gemma.app.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.gemma.app.data.mapper.toDataUrl
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
import org.json.JSONArray
import org.json.JSONObject
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
        images: List<Uri>,
        contentResolver: ContentResolver,
        onToken: (String) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ): EventSource {

        val jsonRequest = buildJsonRequest(
            message = message,
            images = images,
            contentResolver = contentResolver
        )

        val requestBody = jsonRequest.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://192.168.1.30:8080/api/chat")
            .header("Accept", "text/event-stream")
            .post(requestBody)
            .build()

        val listener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.d("LlmRepository", "SSE opened: ${response.code}")
            }

            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                if (data == "[DONE]") return

                try {
                    val response = adapter.fromJson(data)
                    val content = response?.choices?.firstOrNull()?.delta?.content
                    if (!content.isNullOrEmpty()) onToken(content)
                } catch (e: Exception) {
                    Log.e("LlmRepository", "Error parsing SSE data: $data", e)
                }
            }

            override fun onClosed(eventSource: EventSource) {
                onComplete()
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                onError(t ?: kotlinx.io.IOException("Unknown SSE failure"))
            }
        }

        return EventSources.createFactory(client).newEventSource(request, listener)
    }

    private fun buildJsonRequest(
        message: String,
        images: List<Uri>,
        contentResolver: ContentResolver
    ): String {
        val request = JSONObject()
            .put("model", if (images.isEmpty()) "gemma4:e4b" else "gemma4:e4b")
            .put("stream", true)
            .put("messages", JSONArray().put(buildMessageJson(message, images, contentResolver)))

        return request.toString()
    }

    private fun buildMessageJson(
        message: String,
        images: List<Uri>,
        contentResolver: ContentResolver
    ): JSONObject {
        val userMessage = JSONObject().put("role", "user")

        if (images.isEmpty()) {
            userMessage.put("content", message)
            return userMessage
        }

        val content = JSONArray()
            .put(
                JSONObject()
                    .put("type", "text")
                    .put("text", message)
            )

        images.forEach { uri ->
            content.put(
                JSONObject()
                    .put("type", "image_url")
                    .put(
                        "image_url",
                        JSONObject().put("url", uri.toDataUrl(contentResolver))
                    )
            )
        }

        userMessage.put("content", content)
        return userMessage
    }
}


