package com.gemma.app.presentation.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel : ViewModel() {
    private val _themeConfig = MutableStateFlow(AppThemeConfig.SYSTEM)
    val themeConfig = _themeConfig.asStateFlow()

    fun setThemeConfig(config: AppThemeConfig) {
        _themeConfig.value = config
    }
}
