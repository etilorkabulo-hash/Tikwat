package com.aircraftwar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0, 200, 100),
    secondary = Color(255, 150, 0),
    tertiary = Color(100, 150, 255),
    background = Color(20, 20, 40),
    surface = Color(30, 30, 50),
    error = Color(255, 50, 50)
)

@Composable
fun AircraftWarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
