package com.gemma.app.presentation.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.height
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.gemma.app.presentation.chat.ChatMessage
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.background
import android.net.Uri
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.ClipEntry
import android.content.ClipData
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ChatMessageList(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onListenClick: (String) -> Unit = {}
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, messages.lastOrNull()?.text) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size /*- 1*/)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        flingBehavior = ScrollableDefaults.flingBehavior(),
        overscrollEffect = rememberOverscrollEffect(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        itemsIndexed(messages, key = { _, item -> item.id }) { index, message ->
            if (message.isFromUser) {
                UserMessageBubble(message)
            } else {
                val isStreaming = isLoading && index == messages.lastIndex
                GemmaMessageBubble(
                    text = message.text,
                    showOptions = !isStreaming,
                    onListenClick = onListenClick
                )
            }
        }
    }
}

@Composable
fun UserMessageBubble(message: ChatMessage) {
    var previewImageUri by remember { mutableStateOf<Uri?>(null) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (message.images.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    message.images.forEach { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Attached Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .height(110.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { previewImageUri = uri }
                        )
                    }
                }
            }
            if (message.text.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.widthIn( max = 350.dp)
                ) {
                    Text(
                        text = message.text,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }

    if (previewImageUri != null) {
        Dialog(
            onDismissRequest = { previewImageUri = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurface)
                    .clickable { previewImageUri = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = previewImageUri,
                    contentDescription = "Preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                
                IconButton(
                    onClick = { previewImageUri = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

@Composable
fun GemmaMessageBubble(
    text: String,
    showOptions: Boolean = true,
    onListenClick: (String) -> Unit = {}
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = parseMarkdownToAnnotatedString(text),
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
        if (showOptions && text.isNotEmpty()) {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = {
                    scope.launch {
                        clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Gemma Message", text)))
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { onListenClick(text) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


