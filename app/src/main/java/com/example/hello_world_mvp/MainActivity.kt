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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var languageRepository: LanguageRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        // 🌐 저장된 언어를 super.onCreate() 전에 동기적으로 적용
        applyStoredLanguageSync()

        super.onCreate(savedInstanceState)

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
                val savedLanguage = languageRepository.getLanguage()
                val localeList = LocaleListCompat.forLanguageTags(savedLanguage.localeCode)

                android.util.Log.d("MainActivity", "🌐 저장된 언어: ${savedLanguage.displayName} (${savedLanguage.localeCode})")
                android.util.Log.d("MainActivity", "🌐 현재 AppLocales: ${AppCompatDelegate.getApplicationLocales()}")

                AppCompatDelegate.setApplicationLocales(localeList)

                android.util.Log.d("MainActivity", "🌐 언어 적용 완료: ${savedLanguage.localeCode}")
                android.util.Log.d("MainActivity", "🌐 적용 후 AppLocales: ${AppCompatDelegate.getApplicationLocales()}")
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "🌐 언어 적용 실패", e)
            }
        }
    }
}
