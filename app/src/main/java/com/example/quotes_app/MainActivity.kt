package com.example.quotes_app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.quotes_app.presentation.navigation.AppNavigation
import com.example.quotes_app.utils.ThemeManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        
        themeManager = ThemeManager(this)
        themeManager.applyTheme(themeManager.isDarkMode())

        super.onCreate(savedInstanceState)
        
        setContent {
            AppNavigation(themeManager = themeManager)
        }
    }
}