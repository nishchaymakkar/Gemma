package com.gemma.app.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemma.app.data.repository.LlmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.sse.EventSource
import java.util.UUID

class ChatViewModel : ViewModel() {
    
    private val repository = LlmRepository()
    private var currentEventSource: EventSource? = null

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.SendMessage -> {
                val userMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = event.text,
                    isFromUser = true,
                    images = event.images
                )
                
                _uiState.update { it.copy(messages = it.messages + userMessage) }
                
                val responseId = UUID.randomUUID().toString()
                val initialResponse = ChatMessage(
                    id = responseId,
                    text = "",
                    isFromUser = false
                )
                
                _uiState.update { it.copy(messages = it.messages + initialResponse) }
                
                currentEventSource?.cancel()
                currentEventSource = repository.streamChat(
                    message = event.text,
                    onToken = { token ->
                        viewModelScope.launch(Dispatchers.Main) {
                            _uiState.update { state ->
                                val messages = state.messages.map {
                                    if (it.id == responseId) {
                                        it.copy(text = it.text + token)
                                    } else {
                                        it
                                    }
                                }
                                state.copy(messages = messages)
                            }
                        }
                    },
                    onComplete = {
                        // Handle completion
                    },
                    onError = { error ->
                        viewModelScope.launch(Dispatchers.Main) {
                            _uiState.update { state ->
                                val messages = state.messages.map {
                                    if (it.id == responseId) {
                                        it.copy(text = it.text + "\n[Error: ${error.message}]")
                                    } else {
                                        it
                                    }
                                }
                                state.copy(messages = messages)
                            }
                        }
                    }
                )
            }
            is ChatEvent.NewChat -> {
                _uiState.update { it.copy(messages = emptyList()) }
                currentEventSource?.cancel()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentEventSource?.cancel()
    }
}
