package com.gemma.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gemma.app.presentation.chat.ChatScreen
import com.gemma.app.presentation.settings.ProfileScreen
import com.gemma.app.presentation.settings.LanguageViewModel
import com.gemma.app.presentation.theme.AppTheme
import com.gemma.app.presentation.theme.ThemeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val themeConfig by themeViewModel.themeConfig.collectAsState()
            
            val languageViewModel: LanguageViewModel = viewModel()
            val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

            AppTheme(themeConfig = themeConfig) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "chat"
                    ) {
                        composable("chat") {
                            ChatScreen(
                                onProfileClick = {
                                    navController.navigate("profile")
                                }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(
                                themeConfig = themeConfig,
                                onThemeSelect = { themeViewModel.setThemeConfig(it) },
                                selectedLanguage = selectedLanguage,
                                languages = languageViewModel.languages,
                                onLanguageSelect = { language ->
                                    languageViewModel.setSelectedLanguage(language)
                                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language.code)
                                    AppCompatDelegate.setApplicationLocales(appLocale)
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
