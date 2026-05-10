package com.gemma.app.presentation.chat

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean
)

data class ChatUiState(
    val isLoading: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val error: String? = null
)
