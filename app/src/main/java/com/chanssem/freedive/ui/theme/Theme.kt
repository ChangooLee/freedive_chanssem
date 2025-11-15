package com.chanssem.freedive.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MobaBlue,
    secondary = MobaDarkBlue,
    tertiary = MobaLightBlue,
    background = Black,
    surface = Gray900,
    onPrimary = White,
    onSecondary = White,
    onTertiary = Black,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = MobaBlue,
    secondary = MobaDarkBlue,
    tertiary = MobaLightBlue,
    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onTertiary = Black,
    onBackground = Black,
    onSurface = Black
)

@Composable
fun FreediveChanssemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}