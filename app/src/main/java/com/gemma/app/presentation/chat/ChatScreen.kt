package com.gemma.app.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gemma.app.presentation.chat.components.ChatDrawer
import com.gemma.app.presentation.chat.components.ChatInputBar
import com.gemma.app.presentation.chat.components.ChatMessageList
import com.gemma.app.presentation.chat.components.ChatTopBar
import com.gemma.app.presentation.chat.components.EmptyChatState
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ChatDrawer(
                onNewChat = {
                    viewModel.onEvent(ChatEvent.NewChat)
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                ChatTopBar(
                    onMenuClick = { 
                        scope.launch { drawerState.open() } 
                    },
                    onNewChatClick = { viewModel.onEvent(ChatEvent.NewChat) }
                )
            },
            bottomBar = {
                ChatInputBar(
                    onSend = { message ->
                        viewModel.onEvent(ChatEvent.SendMessage(message))
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
                contentAlignment = if (uiState.messages.isEmpty()) Alignment.Center else Alignment.TopCenter
            ) {
                if (uiState.messages.isEmpty()) {
                    EmptyChatState()
                } else {
                    ChatMessageList(messages = uiState.messages)
                }
            }
        }
    }
}
