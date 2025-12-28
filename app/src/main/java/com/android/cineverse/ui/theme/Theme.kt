package com.android.cineverse.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentPink,
    secondary = GradientStart,
    tertiary = GradientEnd,
    background = DarkPurple,
    surface = CardBackground,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun CineVerseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
