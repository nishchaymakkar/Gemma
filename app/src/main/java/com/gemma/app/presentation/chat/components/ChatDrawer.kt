package com.gemma.app.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatDrawer(
    onNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(360.dp),
        drawerContainerColor = Color(0xFFFAFAFA)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Gemma",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color.White,
                        modifier = Modifier.shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(100.dp),
                            spotColor = Color(0x29000000)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            IconButton(
                                onClick = { /* Search */ },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            Surface(
                                shape = RoundedCornerShape(1000.dp),
                                color = Color(0xFF2B7FFF),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "NM",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                val dummyChats = listOf(
                    "Mascot Design Request",
                    "How do I create a unique mascot for my brand?",
                    "What are the key elements to consider in mascot design?",
                    "Can you suggest some popular mascot design styles?"
                )

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(
                            text = "Recent Chats",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    items(dummyChats) { chatTitle ->
                        Text(
                            text = chatTitle,
                            fontSize = 16.sp,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Open chat */ }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }

            // FAB
            FloatingActionButton(
                onClick = onNewChat,
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = RoundedCornerShape(65.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "New Chat",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Chat",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
