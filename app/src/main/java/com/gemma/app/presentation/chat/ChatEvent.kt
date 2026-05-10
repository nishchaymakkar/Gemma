package com.gemma.app.presentation.chat

sealed class ChatEvent {
    data class SendMessage(val text: String, val images: List<android.net.Uri> = emptyList()) : ChatEvent()
    data object NewChat : ChatEvent()
}
