package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ExamDarkColorScheme = darkColorScheme(
  primary = DarkAccentCyan,
  onPrimary = Color(0xFF041226),
  primaryContainer = DarkSurfaceVariant,
  onPrimaryContainer = DarkAccentCyan,
  secondary = DarkAccentBlue,
  onSecondary = Color(0xFF041226),
  background = DarkBackground,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkTextSecondary,
  outline = DarkBorder,
  error = StatusDanger,
  onError = Color.White
)

val ExamLightColorScheme = lightColorScheme(
  primary = LightAccentBlue,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFE0E7FF),
  onPrimaryContainer = Color(0xFF1E3A8A),
  secondary = Color(0xFF0284C7),
  onSecondary = Color.White,
  background = LightBackground,
  onBackground = LightTextPrimary,
  surface = LightSurface,
  onSurface = LightTextPrimary,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = LightTextSecondary,
  outline = LightBorder,
  error = StatusDanger,
  onError = Color.White
)

enum class ThemeMode {
  UNSET,
  DARK,
  LIGHT
}

@Composable
fun ExamBrowserTheme(
  themeMode: ThemeMode = ThemeMode.DARK,
  content: @Composable () -> Unit
) {
  val isDark = when (themeMode) {
    ThemeMode.DARK -> true
    ThemeMode.LIGHT -> false
    ThemeMode.UNSET -> isSystemInDarkTheme()
  }

  val colorScheme = if (isDark) ExamDarkColorScheme else ExamLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}
