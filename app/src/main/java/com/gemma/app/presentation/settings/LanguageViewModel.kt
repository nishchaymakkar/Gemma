package com.gemma.app.presentation.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LanguageViewModel : ViewModel() {
    private val _languages = listOf(
        Language("English", "en"),
        Language("Hindi", "hi"),
        Language("Spanish", "es"),
        Language("French", "fr"),
        Language("German", "de")
    )
    val languages = _languages

    private val _selectedLanguage = MutableStateFlow(getCurrentLanguage())
    val selectedLanguage = _selectedLanguage.asStateFlow()

    private fun getCurrentLanguage(): Language {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val currentTag = if (currentLocales.isEmpty) "en" else currentLocales.toLanguageTags()
        return _languages.find { it.code == currentTag } ?: _languages[0]
    }

    fun setSelectedLanguage(language: Language) {
        _selectedLanguage.value = language
    }
}
