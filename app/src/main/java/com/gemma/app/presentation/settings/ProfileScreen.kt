package com.gemma.app.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.res.stringResource
import com.gemma.app.R
import com.gemma.app.presentation.theme.AppTheme
import com.gemma.app.presentation.theme.AppThemeConfig
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import kotlinx.coroutines.launch

data class Language(
    val name: String,
    val code: String
)

@Composable
fun ProfileScreen(
    themeConfig: AppThemeConfig,
    onThemeSelect: (AppThemeConfig) -> Unit,
    selectedLanguage: Language,
    languages: List<Language>,
    onLanguageSelect: (Language) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showThemePicker by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = CircleShape,
                            spotColor = Color(0X29000000),
                            //ambientColor = Color(0X29000000)
                        ),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceBright
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            
            // Profile Avatar
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.inverseSurface,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "NM",
                        color = MaterialTheme.colorScheme.surface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Nishchay Makkar",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // App settings section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_settings),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
            
            SettingsItem(
                icon = Icons.Outlined.WbSunny,
                title = stringResource(R.string.appearance),
                subtitle = when (themeConfig) {
                    AppThemeConfig.SYSTEM -> stringResource(R.string.system_default)
                    AppThemeConfig.LIGHT -> stringResource(R.string.light)
                    AppThemeConfig.DARK -> stringResource(R.string.dark)
                },
                onClick = { showThemePicker = true },
            )
            
            SettingsItem(
                icon = Icons.Outlined.Language,
                title = stringResource(R.string.app_language),
                subtitle = when(selectedLanguage.code) {
                    "en" -> stringResource(R.string.lang_en)
                    "hi" -> stringResource(R.string.lang_hi)
                    "es" -> stringResource(R.string.lang_es)
                    "fr" -> stringResource(R.string.lang_fr)
                    "de" -> stringResource(R.string.lang_de)
                    else -> selectedLanguage.name
                },
                onClick = { showLanguagePicker = true }
            )

            if (showThemePicker) {
                ThemePickerOverlay(
                    selectedConfig = themeConfig,
                    onConfigSelect = {
                        onThemeSelect(it)
                        showThemePicker = false
                    },
                    onDismiss = { showThemePicker = false }
                )
            }

            if (showLanguagePicker) {
                LanguageBottomSheet(
                    selectedLanguage = selectedLanguage,
                    languages = languages,
                    onLanguageSelect = {
                        onLanguageSelect(it)
                        showLanguagePicker = false
                    },
                    onDismiss = { showLanguagePicker = false }
                )
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun LanguageBottomSheet(
    selectedLanguage: Language,
    languages: List<Language>,
    onLanguageSelect: (Language) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(100.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.select_language),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            languages.forEach { language ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onLanguageSelect(language)
                                }
                            }
                        }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = language.code == selectedLanguage.code,
                        onClick = null // Row handles click
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = when(language.code) {
                            "en" -> stringResource(R.string.lang_en)
                            "hi" -> stringResource(R.string.lang_hi)
                            "es" -> stringResource(R.string.lang_es)
                            "fr" -> stringResource(R.string.lang_fr)
                            "de" -> stringResource(R.string.lang_de)
                            else -> language.name
                        },
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun ThemePickerOverlay(
    selectedConfig: AppThemeConfig,
    onConfigSelect: (AppThemeConfig) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .shadow(
                    elevation = 36.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color(0x29000000)
                ),
            border = BorderStroke(width = .5.dp, color = MaterialTheme.colorScheme.surfaceBright)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                ThemeOptionRow(
                    text = stringResource(R.string.system_default),
                    isSelected = selectedConfig == AppThemeConfig.SYSTEM,
                    onClick = { onConfigSelect(AppThemeConfig.SYSTEM) }
                )
                Spacer(modifier = Modifier.height(18.dp))
                ThemeOptionRow(
                    text = stringResource(R.string.light),
                    isSelected = selectedConfig == AppThemeConfig.LIGHT,
                    onClick = { onConfigSelect(AppThemeConfig.LIGHT) }
                )
                Spacer(modifier = Modifier.height(18.dp))
                ThemeOptionRow(
                    text = stringResource(R.string.dark),
                    isSelected = selectedConfig == AppThemeConfig.DARK,
                    onClick = { onConfigSelect(AppThemeConfig.DARK) }
                )
            }
        }
    }
}

@Composable
fun ThemeOptionRow(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(20.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    AppTheme {
        ProfileScreen(
            themeConfig = AppThemeConfig.SYSTEM,
            onThemeSelect = {},
            selectedLanguage = Language("English", "en"),
            languages = listOf(Language("English", "en"), Language("Hindi", "hi")),
            onLanguageSelect = {},
            onBackClick = {}
        )
    }
}
