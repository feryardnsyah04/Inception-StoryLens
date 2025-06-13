package com.inception.storylens.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = MidnightBlue,       // Warna utama untuk tombol, FAB, etc.
    onPrimary = White,            // Warna teks di atas komponen 'primary'
    secondary = CornflowerBlue,   // Warna sekunder untuk filter chips, etc.
    onSecondary = White,
    background = LightSkyBlue,    // Warna latar belakang utama aplikasi
    onBackground = Black,         // Warna teks di atas 'background'
    surface = GhostWhite,         // Warna untuk permukaan komponen seperti Card, Sheet
    onSurface = Black,            // Warna teks di atas 'surface'
    error = Color.Red,            // Warna untuk menunjukkan error
    onError = White
)

@Composable
fun StoryLensTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}