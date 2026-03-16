package com.example.hello_world_mvp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import com.example.core.data.common.LanguageRepository
import com.example.core.ui.theme.HelloWorldTheme
import com.example.feature.MainScreen
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MainActivityEntryPoint {
        fun languageRepository(): LanguageRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyStoredLanguageSync()

        enableEdgeToEdge()

        setContent {
            HelloWorldTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    private fun applyStoredLanguageSync() {
        runBlocking {
            try {
                val languageRepository = EntryPointAccessors.fromApplication(
                    applicationContext,
                    MainActivityEntryPoint::class.java
                ).languageRepository()

                val savedLanguage = languageRepository.getLanguage()
                val localeList = LocaleListCompat.forLanguageTags(savedLanguage.localeCode)

                android.util.Log.d("MainActivity", "Stored language: ${savedLanguage.displayName} (${savedLanguage.localeCode})")
                android.util.Log.d("MainActivity", "Current AppLocales: ${AppCompatDelegate.getApplicationLocales()}")

                AppCompatDelegate.setApplicationLocales(localeList)

                android.util.Log.d("MainActivity", "Applied language: ${savedLanguage.localeCode}")
                android.util.Log.d("MainActivity", "After apply AppLocales: ${AppCompatDelegate.getApplicationLocales()}")
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Language apply failed", e)
            }
        }
    }
}
