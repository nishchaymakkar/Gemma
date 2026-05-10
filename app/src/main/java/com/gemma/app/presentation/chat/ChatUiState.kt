package com.gemma.app.presentation.chat

data class ChatUiState(
    val isLoading: Boolean = false,
    val messages: List<String> = emptyList(),
    val error: String? = null
)
