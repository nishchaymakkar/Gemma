package com.gemma.app.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.gemma.app.presentation.chat.components.ChatInputBar
import com.gemma.app.presentation.chat.components.ChatTopBar
import com.gemma.app.presentation.chat.components.EmptyChatState
@Preview
@Composable
fun ChatScreen() {
    Scaffold(
        topBar = {
            ChatTopBar(
                onMenuClick = { /* Handle Menu Click */ },
                onNewChatClick = { /* Handle New Chat Click */ }
            )
        },
        bottomBar = {
            ChatInputBar(
                onSend = { message ->
                    // TODO: Dispatch send event to ViewModel
                }
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFAFAFA)),
            contentAlignment = Alignment.Center
        ) {
            EmptyChatState()
        }
    }
}
