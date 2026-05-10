package com.gemma.app.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AttachmentMenu(
    onCameraClick: () -> Unit,
    onPhotoClick: () -> Unit,
    onFileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(16.dp)
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0x29000000),
                ambientColor = Color(0x29000000)
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            AttachmentMenuItem(
                icon = Icons.Outlined.PhotoCamera,
                label = "Camera",
                onClick = onCameraClick
            )
            AttachmentMenuItem(
                icon = Icons.Outlined.Image,
                label = "Photo",
                onClick = onPhotoClick
            )
//            AttachmentMenuItem(
//                icon = Icons.Outlined.AttachFile,
//                label = "File",
//                onClick = onFileClick
//            )
        }
    }
}

@Composable
private fun AttachmentMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFF0F0F0),
            modifier = Modifier.size(40.dp) // Adjusted to fit 24dp icon with 8dp padding
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(13.dp))
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0D0D0D)
        )
    }
}


