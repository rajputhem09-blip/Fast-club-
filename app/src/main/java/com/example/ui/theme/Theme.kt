package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KhelvColorScheme = darkColorScheme(
  primary = KhelvBluePrimary,
  onPrimary = Color.White,
  primaryContainer = KhelvBlueLight,
  onPrimaryContainer = Color.White,
  secondary = KhelvGold,
  onSecondary = Color.Black,
  secondaryContainer = KhelvNavyCard,
  onSecondaryContainer = KhelvGoldLight,
  tertiary = KhelvOrange,
  onTertiary = Color.White,
  background = KhelvNavyDark,
  onBackground = KhelvTextPrimary,
  surface = KhelvNavySurface,
  onSurface = KhelvTextPrimary,
  surfaceVariant = KhelvNavyCard,
  onSurfaceVariant = KhelvTextSecondary,
  outline = KhelvNavyBorder,
  error = KhelvRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = KhelvColorScheme,
    typography = Typography,
    content = content
  )
}
