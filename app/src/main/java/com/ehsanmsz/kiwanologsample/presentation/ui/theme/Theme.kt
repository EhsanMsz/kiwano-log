package com.ehsanmsz.kiwanologsample.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun KiwanoLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) kiwanoDarkColorScheme else kiwanoLightColorScheme,
        typography = kiwanoTypography,
        content = content
    )
}