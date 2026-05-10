package com.gemma.app.presentation.chat.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.io.File
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.net.toUri

@Composable
fun ChatInputBar(
    onSend: (String, List<Uri>) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by rememberSaveable { mutableStateOf("") }
    var showAttachmentMenu by remember { mutableStateOf(false) }
    var selectedImagesUris by rememberSaveable { mutableStateOf(emptyList<String>()) }
    val selectedImages = selectedImagesUris.map { it.toUri() }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var cameraUri by rememberSaveable { mutableStateOf<String?>(null) }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 3)
    ) { uris ->
        if (uris.isNotEmpty()) {
            scope.launch(Dispatchers.IO) {
                val compressedUris = uris.map { 
                    com.gemma.app.util.ImageCompressor.compressImage(context, it).toString()
                }
                withContext(Dispatchers.Main) {
                    selectedImagesUris = (selectedImagesUris + compressedUris).take(3)
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            scope.launch(Dispatchers.IO) {
                val uri = cameraUri!!.toUri()
                val compressedUri = com.gemma.app.util.ImageCompressor.compressImage(context, uri)
                withContext(Dispatchers.Main) {
                    selectedImagesUris = (selectedImagesUris + compressedUri.toString()).take(3)
                }
            }
        }
    }

    if (selectedImages.isEmpty()) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .shadow(elevation = 16.dp,
                    spotColor = Color(0x29000000),
                    ambientColor = Color(0x29000000)),
            shape = RoundedCornerShape(100.dp),
            color = Color(0xFFFFFFFF)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    IconButton(
                        onClick = { showAttachmentMenu = !showAttachmentMenu },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Files",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (showAttachmentMenu) {
                        androidx.compose.ui.window.Popup(
                            alignment = Alignment.TopStart,
                            offset = androidx.compose.ui.unit.IntOffset(0, -480),
                            onDismissRequest = { showAttachmentMenu = false }
                        ) {
                            AttachmentMenu(
                                onCameraClick = { 
                                    showAttachmentMenu = false
                                    val file = File(context.cacheDir, "camera_image_${System.currentTimeMillis()}.jpg")
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                    cameraUri = uri.toString()
                                    cameraLauncher.launch(uri)
                                },
                                onPhotoClick = { 
                                    showAttachmentMenu = false
                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                onFileClick = { showAttachmentMenu = false }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF292929)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            if (text.isEmpty()) {
                                Text(
                                    text = "Ask Gemma",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = Color.Black
                ) {
                    IconButton(
                        onClick = {
                            if (text.isNotBlank()) {
                                onSend(text, selectedImages)
                                text = ""
                                selectedImagesUris = emptyList()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box {
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(elevation = 16.dp, spotColor = Color(0x29000000), ambientColor = Color(0x29000000), shape = CircleShape),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    IconButton(onClick = { showAttachmentMenu = !showAttachmentMenu }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Files", modifier = Modifier.size(20.dp))
                    }
                }
                if (showAttachmentMenu) {
                    androidx.compose.ui.window.Popup(
                        alignment = Alignment.TopStart,
                        offset = androidx.compose.ui.unit.IntOffset(0, -650),
                        onDismissRequest = { showAttachmentMenu = false }
                    ) {
                        AttachmentMenu(
                            onCameraClick = { 
                                showAttachmentMenu = false
                                val file = File(context.cacheDir, "camera_image_${System.currentTimeMillis()}.jpg")
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                cameraUri = uri.toString()
                                cameraLauncher.launch(uri)
                            },
                            onPhotoClick = { 
                                showAttachmentMenu = false
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            onFileClick = { showAttachmentMenu = false }
                        )
                    }
                }
            }
            
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 16.dp, spotColor = Color(0x29000000), ambientColor = Color(0x29000000), shape = RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedImages) { uri ->
                            Box(
                                modifier = Modifier
                                    .size(width = 80.dp, height = 110.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF0F0F0))
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Attachment",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(28.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White
                                ) {
                                    IconButton(
                                        onClick = { selectedImagesUris = selectedImagesUris.filter { it != uri.toString() } }
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp), tint = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 4.dp, vertical = 0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 6.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = text,
                                onValueChange = { text = it },
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    color = Color(0xFF292929)
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { innerTextField ->
                                    if (text.isEmpty()) {
                                        Text(
                                            text = "Ask Gemma",
                                            color = Color.Gray,
                                            fontSize = 16.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }

                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = Color.Black
                        ) {
                            IconButton(
                                onClick = {
                                    if (text.isNotBlank() || selectedImages.isNotEmpty()) {
                                        onSend(text, selectedImages)
                                        text = ""
                                        selectedImagesUris = emptyList()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
