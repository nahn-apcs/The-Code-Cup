package com.example.coffee_application

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.coffee_application.ui.navigation.AppNavGraph
import com.example.coffee_application.ui.theme.Coffee_ApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var languageViewModel: LanguageViewModel

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("lang", java.util.Locale.getDefault().language) ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageViewModel = LanguageViewModel(application)

        setContent {
            Coffee_ApplicationTheme {

                val currentLang by languageViewModel.language.collectAsState()

                LaunchedEffect(currentLang) {
                    val baseContext = baseContext
                    val newContext = LocaleHelper.setLocale(baseContext, currentLang)
                    if (baseContext.resources.configuration.locales[0].language != currentLang) {
                        recreate()
                    }
                }

                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    languageViewModel = languageViewModel
                )
            }
        }
    }
}