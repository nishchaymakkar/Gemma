package com.gemma.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gemma.app.presentation.chat.ChatScreen
import com.gemma.app.presentation.splash.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onNavigateToChat = {
                                    navController.navigate("chat") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("chat") {
                            ChatScreen()
                        }
                    }
                }
            }
        }
    }
}
