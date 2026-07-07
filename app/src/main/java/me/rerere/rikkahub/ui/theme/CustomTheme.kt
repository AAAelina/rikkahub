package me.rerere.rikkahub.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFE94560),
    secondary = androidx.compose.ui.graphics.Color(0xFF0F3460),
    tertiary = androidx.compose.ui.graphics.Color(0xFF16213E),
    background = androidx.compose.ui.graphics.Color(0xFF1A1A2E),
    surface = androidx.compose.ui.graphics.Color(0xFF16213E),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color(0xFFEEEEEE),
    onSurface = androidx.compose.ui.graphics.Color(0xFFEEEEEE),
)

@Composable
fun RikkaHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
