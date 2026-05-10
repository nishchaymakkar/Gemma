package com.gemma.app.presentation.chat

sealed class ChatEvent {
    data class SendMessage(val text: String) : ChatEvent()
}
