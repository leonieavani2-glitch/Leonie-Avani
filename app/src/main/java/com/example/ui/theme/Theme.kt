package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val InstitutionalDarkColorScheme = darkColorScheme(
    primary = CryptoGoldPrimary,
    onPrimary = Color(0xFF1E1700),
    primaryContainer = CryptoGoldDark,
    onPrimaryContainer = CryptoGoldLight,
    secondary = ElectricCyan,
    onSecondary = Color(0xFF003549),
    secondaryContainer = Color(0xFF004D6B),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = EmeraldSuccess,
    onTertiary = Color(0xFF003823),
    background = VaultDarkBg,
    onBackground = TextPrimary,
    surface = VaultSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = VaultSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = VaultBorder,
    error = CoralError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our curated institutional colors
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = InstitutionalDarkColorScheme,
        typography = Typography,
        content = content
    )
}
