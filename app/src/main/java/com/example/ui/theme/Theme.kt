package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ShelfDarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    secondary = AccentBlue,
    tertiary = WarnAmber,
    background = BgDark,
    surface = CardDark,
    surfaceVariant = Card2Dark,
    onPrimary = Color0B0F1A,
    onBackground = TextLight,
    onSurface = TextLight,
    error = DangerRed
)

@Composable
fun ShelfInventoryTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ShelfDarkColorScheme,
        typography = Typography,
        content = content
    )
}
