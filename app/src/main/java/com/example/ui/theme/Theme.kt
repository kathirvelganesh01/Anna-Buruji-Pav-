package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = StreetGold,
    onPrimary = TextDark,
    primaryContainer = StreetAmber,
    onPrimaryContainer = TextWhite,
    secondary = ChiliRed,
    onSecondary = TextWhite,
    secondaryContainer = ChiliRedContainer,
    onSecondaryContainer = TextCream,
    tertiary = MintGreen,
    onTertiary = TextWhite,
    background = StallBlack,
    onBackground = TextCream,
    surface = StallDarkSurface,
    onSurface = TextWhite,
    surfaceVariant = StallCardBg,
    onSurfaceVariant = TextCream,
    outline = StallCardBorder,
    error = ChiliRed,
    onError = TextWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Poster is intrinsically styled in iconic black & gold
    dynamicColor: Boolean = false, // Preserve brand identity from poster
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
