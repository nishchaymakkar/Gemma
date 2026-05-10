package com.gemma.app.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.SendMessage -> {
                val userMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = event.text,
                    isFromUser = true
                )
                
                _uiState.update { it.copy(messages = it.messages + userMessage) }
                
                // Simulate network delay and reply
                viewModelScope.launch {
                    delay(1000) // 1 second delay
                    val dummyResponse = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        text = """
                            Here’s a top- notch match latte recipe that balnces flavor, creminess and touch of sweetness- perfect hot or iced.

                            ## Best Macha Latte Recipe (Cafe- Style)

                            **Ingredients** (for 1 serving):
                            * **1 tsp ceremonial-grade matcha** (use culinary-grade only if you’re on a budget)
                            * **2 oz hot water** (about 175°F / 80° C - not boiling)
                            * **6 oz milk** ( dairy or non-dairy like oat, almond, or soy)
                        """.trimIndent(),
                        isFromUser = false
                    )
                    _uiState.update { it.copy(messages = it.messages + dummyResponse) }
                }
            }
            is ChatEvent.NewChat -> {
                _uiState.update { it.copy(messages = emptyList()) }
            }
        }
    }
}
