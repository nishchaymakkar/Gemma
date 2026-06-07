package com.gemma.app.presentation.chat

sealed class ChatEvent {
    data class SendMessage(val text: String, val images: List<android.net.Uri> = emptyList(), val webSearch: Boolean = false) : ChatEvent()
    data object NewChat : ChatEvent()
    data class SelectModel(val model: String) : ChatEvent()
}
