package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val AppColorScheme =
  darkColorScheme(
    primary = Accent,
    background = BackgrounDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onPrimary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextPrimary,
    outline = DividerColor
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = AppColorScheme, typography = Typography, content = content)
}
