package com.gemma.app.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gemma.app.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToChat: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        delay(2000L) // Simulate splash screen delay
        onNavigateToChat()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // We use the launcher icon as a placeholder. 
        // Please replace R.mipmap.ic_launcher with your actual exported Figma logo (e.g. R.drawable.ic_splash_logo)
        Image(
            painter = painterResource(id = R.drawable.gemma_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(150.dp)
        )
    }
}
