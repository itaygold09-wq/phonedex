package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class PhonedexThemeMode {
    RED, BLUE, GREEN, BLACK, WHITE
}

@Composable
fun PhonedexTheme(
    themeMode: PhonedexThemeMode = PhonedexThemeMode.RED,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        PhonedexThemeMode.RED -> {
            if (darkTheme) {
                darkColorScheme(
                    primary = RedPrimaryDark,
                    secondary = RedSecondaryDark,
                    background = RedBackgroundDark,
                    surface = RedSurfaceDark,
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            } else {
                lightColorScheme(
                    primary = RedPrimaryLight,
                    secondary = RedSecondaryLight,
                    background = RedBackgroundLight,
                    surface = RedSurfaceLight,
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            }
        }
        PhonedexThemeMode.BLUE -> {
            if (darkTheme) {
                darkColorScheme(
                    primary = BluePrimaryDark,
                    secondary = BlueSecondaryDark,
                    background = BlueBackgroundDark,
                    surface = BlueSurfaceDark,
                    onPrimary = Color.Black,
                    onSecondary = Color.White,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            } else {
                lightColorScheme(
                    primary = BluePrimaryLight,
                    secondary = BlueSecondaryLight,
                    background = BlueBackgroundLight,
                    surface = BlueSurfaceLight,
                    onPrimary = Color.White,
                    onSecondary = Color.Black,
                    onBackground = Color(0xFF031627),
                    onSurface = Color(0xFF031627)
                )
            }
        }
        PhonedexThemeMode.GREEN -> {
            if (darkTheme) {
                darkColorScheme(
                    primary = GreenPrimaryDark,
                    secondary = GreenSecondaryDark,
                    background = GreenBackgroundDark,
                    surface = GreenSurfaceDark,
                    onPrimary = Color.Black,
                    onSecondary = Color.White,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            } else {
                lightColorScheme(
                    primary = GreenPrimaryLight,
                    secondary = GreenSecondaryLight,
                    background = GreenBackgroundLight,
                    surface = GreenSurfaceLight,
                    onPrimary = Color.White,
                    onSecondary = Color.Black,
                    onBackground = Color(0xFF021706),
                    onSurface = Color(0xFF021706)
                )
            }
        }
        PhonedexThemeMode.BLACK -> {
            if (darkTheme) {
                darkColorScheme(
                    primary = BlackPrimaryDark,
                    secondary = BlackSecondaryDark,
                    background = BlackBackgroundDark,
                    surface = BlackSurfaceDark,
                    onPrimary = Color.Black,
                    onSecondary = Color.White,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            } else {
                lightColorScheme(
                    primary = BlackPrimaryLight,
                    secondary = BlackSecondaryLight,
                    background = BlackBackgroundLight,
                    surface = BlackSurfaceLight,
                    onPrimary = Color.White,
                    onSecondary = Color.Black,
                    onBackground = Color(0xFF1C1C1C),
                    onSurface = Color(0xFF1C1C1C)
                )
            }
        }
        PhonedexThemeMode.WHITE -> {
            if (darkTheme) {
                darkColorScheme(
                    primary = WhitePrimaryDark,
                    secondary = WhiteSecondaryDark,
                    background = WhiteBackgroundDark,
                    surface = WhiteSurfaceDark,
                    onPrimary = Color.Black,
                    onSecondary = Color.White,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            } else {
                lightColorScheme(
                    primary = WhitePrimaryLight,
                    secondary = WhiteSecondaryLight,
                    background = WhiteBackgroundLight,
                    surface = WhiteSurfaceLight,
                    onPrimary = Color.Black,
                    onSecondary = Color.Black,
                    onBackground = Color(0xFF2E2E2E),
                    onSurface = Color(0xFF2E2E2E)
                )
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
