package com.gemma.app.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gemma.app.presentation.chat.components.ChatDrawer
import com.gemma.app.presentation.chat.components.ChatInputBar
import com.gemma.app.presentation.chat.components.ChatMessageList
import com.gemma.app.presentation.chat.components.ChatTopBar
import com.gemma.app.presentation.chat.components.EmptyChatState
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import android.speech.tts.TextToSpeech
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect
import java.util.Locale
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.remember
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset

@Composable
fun ChatScreen(
    onProfileClick: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    var activeAudioMessage by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ChatDrawer(
                onNewChat = {
                    viewModel.onEvent(ChatEvent.NewChat)
                    scope.launch { drawerState.close() }
                },
                onProfileClick = {
                    scope.launch { drawerState.close() }
                    onProfileClick()
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize().windowInsetsPadding(insets = WindowInsets.safeContent)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (uiState.isLoading) {
                MeshGradientBackground()
            }

            
            Scaffold(
                modifier = Modifier.imePadding(),
                topBar = {
                    ChatTopBar(
                        selectedModel = uiState.selectedModel,
                        onModelSelected = { model ->
                            viewModel.onEvent(ChatEvent.SelectModel(model))
                        },
                        onMenuClick = { 
                            scope.launch { drawerState.open() } 
                        },
                        onNewChatClick = { viewModel.onEvent(ChatEvent.NewChat) }
                    )
                },
                bottomBar = {
                    ChatInputBar(
                        isLoading = uiState.isLoading,
                        onSend = { message, images, webSearch ->
                            viewModel.onEvent(ChatEvent.SendMessage(message, images, webSearch))
                        }
                    )
                },
                containerColor = Color.Transparent
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = if (uiState.messages.isEmpty()) Alignment.Center else Alignment.TopCenter
                ) {
                    if (uiState.messages.isEmpty()) {
                        EmptyChatState()
                    } else {
                        ChatMessageList(
                            messages = uiState.messages,
                            isLoading = uiState.isLoading,
                            onListenClick = { messageText ->
                                activeAudioMessage = messageText
                                tts?.speak(messageText, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        )
                    }

                    if (activeAudioMessage != null) {
                    AudioPlayerOverlay(
                        onClose = {
                            tts?.stop()
                            activeAudioMessage = null
                        },
                        onPlay = {
                            activeAudioMessage?.let {
                                tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    )
                }
            }
        }
    }
}
}

@Composable
fun AudioPlayerOverlay(
    onClose: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(32.dp),
        modifier = modifier.fillMaxWidth(0.9f)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPlay,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSurface, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "00:00", 
                color = MaterialTheme.colorScheme.onSurfaceVariant, 
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Mock waveform
                val heights = listOf(4.dp, 8.dp, 12.dp, 6.dp, 10.dp, 16.dp, 12.dp, 8.dp, 4.dp, 10.dp, 14.dp, 8.dp, 6.dp, 12.dp, 10.dp, 4.dp, 8.dp, 12.dp)
                heights.forEach { h ->
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(h)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    )
                }
            }
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun MeshGradientBackground() {

    val transition = rememberInfiniteTransition(label = "")

    val offset1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val offset2 by transition.animateFloat(
        initialValue = 1000f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val color1 by transition.animateColor(
        initialValue = Color(0xFF00FF99).copy(alpha = 0.5f),
        targetValue = Color(0xFFFFA5E6).copy(alpha = 0.5f),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color1"
    )

    val color2 by transition.animateColor(
        initialValue = Color(0xFF3F51B5).copy(alpha = 0.4f),
        targetValue = Color(0xFFFF9800).copy(alpha = 0.4f),
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color2"
    )

    val color3 by transition.animateColor(
        initialValue = Color(0xFFFF1300).copy(alpha = 0.35f),
        targetValue = Color(0xFFCDDC39).copy(alpha = 0.35f),
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color3"
    )

    Canvas(
        modifier = Modifier.height(50.dp).fillMaxWidth()
    ) {

        // Base dark background
        //drawRect(Color(0xFF0B0B12))

        // Purple blob
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color1,
                    Color.Transparent
                ),
                center = Offset(offset1, size.height * 0.3f),
                radius = size.width * 0.7f
            ),
            radius = size.width * 0.7f,
            center = Offset(offset1, size.height * 0.3f)
        )

        // Blue blob
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color2,
                    Color.Transparent
                ),
                center = Offset(size.width - offset2, size.height * 0.6f),
                radius = size.width * 0.8f
            ),
            radius = size.width * 0.8f,
            center = Offset(size.width - offset2, size.height * 0.6f)
        )

        // Pink blob
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color3,
                    Color.Transparent
                ),
                center = Offset(size.width * 0.5f, offset2),
                radius = size.width * 0.65f
            ),
            radius = size.width * 0.65f,
            center = Offset(size.width * 0.5f, offset2)
        )
    }
}
