package com.example.coffee_application

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class LanguageViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(prefs.getString("lang", Locale.getDefault().language) ?: "en")
    val language: StateFlow<String> = _language

    fun switchLanguage() {
        val newLang = if (_language.value == "vi") "en" else "vi"
        setLanguage(newLang)
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        prefs.edit().putString("lang", lang).apply()
    }
}
