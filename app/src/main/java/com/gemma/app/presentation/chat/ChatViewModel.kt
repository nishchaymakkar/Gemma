package com.gemma.app.presentation.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gemma.app.data.repository.LlmRepository
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.sse.EventSource
import java.util.UUID

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LlmRepository()
    private var currentEventSource: EventSource? = null

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.SendMessage -> sendMessage(event)
            ChatEvent.NewChat -> newChat()
            is ChatEvent.SelectModel -> selectModel(event.model)
        }
    }

    private fun sendMessage(event: ChatEvent.SendMessage) {
        val trimmedMessage = event.text.trim()
        if (trimmedMessage.isEmpty() && event.images.isEmpty()) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = trimmedMessage,
            isFromUser = true,
            images = event.images
        )

        val responseId = UUID.randomUUID().toString()
        val initialResponse = ChatMessage(
            id = responseId,
            text = "",
            isFromUser = false
        )

        _uiState.update { state ->
            state.copy(
                isLoading = true,
                error = null,
                messages = state.messages + userMessage + initialResponse
            )
        }

        currentEventSource?.cancel()
        currentEventSource = repository.streamChat(
            message = trimmedMessage,
            images = event.images,
            webSearch = event.webSearch,
            model = uiState.value.selectedModel,
            onToken = { token ->
                _uiState.update { state ->
                    state.copy(
                        messages = state.messages.map { message ->
                            if (message.id == responseId) {
                                message.copy(text = message.text + token)
                            } else {
                                message
                            }
                        }
                    )
                }
            },
            onComplete = {
                _uiState.update { state -> state.copy(isLoading = false) }
            },
            contentResolver = getApplication<Application>().contentResolver,
            onError = { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = error.message,
                        messages = state.messages.map { message ->
                            if (message.id == responseId) {
                                message.copy(text = message.text + "\n[Error: ${error.message ?: "Unknown error"}]")
                            } else {
                                message
                            }
                        }
                    )
                }
            }
        )
    }

    private fun newChat() {
        currentEventSource?.cancel()
        currentEventSource = null
        _uiState.update { ChatUiState() }
    }

    private fun selectModel(model: String) {
        _uiState.update { it.copy(selectedModel = model) }
    }

    override fun onCleared() {
        currentEventSource?.cancel()
        currentEventSource = null
        super.onCleared()
    }
}
