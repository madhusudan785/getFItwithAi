package com.example.dietplanner.data.network

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

class SSEManager(private val baseUrl: String) {

    companion object {
        private const val TAG = "SSEManager"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun streamDietPlan(requestBody: String): Flow<StreamEvent> = callbackFlow {
        Log.d(TAG, "=== Starting SSE Connection ===")
        Log.d(TAG, "Base URL: $baseUrl")
        Log.d(TAG, "Request Body: $requestBody")

        val url = "$baseUrl/api/diet/generate"
        Log.d(TAG, "Full URL: $url")

        val request = Request.Builder()
            .url(url)
            .post(
                okhttp3.RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    requestBody
                )
            )
            .header("Accept", "text/event-stream")
            .header("Content-Type", "application/json")
            .build()

        Log.d(TAG, "Request headers: ${request.headers}")

        val eventSourceListener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.i(TAG, "✅ SSE Connection opened")
                Log.d(TAG, "Response code: ${response.code}")
                Log.d(TAG, "Response headers: ${response.headers}")
                trySend(StreamEvent.Connected)
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                Log.d(TAG, "📨 Event received - ID: $id, Type: $type")
                Log.d(TAG, "Data: $data")

                if (data == "[DONE]") {
                    Log.i(TAG, "✅ Stream completed with [DONE] marker")
                    trySend(StreamEvent.Complete)
                    eventSource.cancel()
                } else {
                    Log.d(TAG, "📤 Sending data chunk (${data.length} chars)")
                    trySend(StreamEvent.Data(data))
                }
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?
            ) {
                val message = t?.message ?: "Unknown error"

                // Ignore normal socket closure after [DONE]
                if (message.contains("Socket closed", ignoreCase = true) && response?.code == 200) {
                    Log.i("SSEManager", "✅ Stream closed gracefully.")
                    return
                }

                Log.e("SSEManager", "❌ SSE Connection failed (Ask Gemini)", t)
                Log.e("SSEManager", "Error message: $message")
                Log.e("SSEManager", "Response code: ${response?.code}")
                Log.e("SSEManager", "Response body: ${response?.body?.string()}")
            }

            override fun onClosed(eventSource: EventSource) {
                Log.i(TAG, "🔒 SSE Connection closed")
                trySend(StreamEvent.Complete)
            }
        }

        val eventSource = EventSources.createFactory(client)
            .newEventSource(request, eventSourceListener)

        awaitClose {
            Log.d(TAG, "🧹 Cleaning up SSE connection")
            eventSource.cancel()
        }
    }
}

sealed class StreamEvent {
    object Connected : StreamEvent()
    data class Data(val content: String) : StreamEvent()
    data class Error(val message: String) : StreamEvent()
    object Complete : StreamEvent()
}