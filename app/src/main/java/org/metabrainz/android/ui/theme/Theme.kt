package org.metabrainz.android.ui.theme

import android.app.Activity
import android.content.Context
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.preference.PreferenceManager
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/** Theme for the whole app. */

private val DarkColorScheme = darkColorScheme(
    background = app_bg_night,
    onBackground = app_bg_light,
    primary = app_bg_night,
    onSurface = Color.White     // Text color (Which is ON surface/canvas)
)

private val LightColorScheme = lightColorScheme(
    background = app_bg_day,
    onBackground = app_bg_light,
    primary = app_bg_day,
    onSurface = Color.Black
)

/**
 * This variable defines the ui mode of the system.
 *
 * If Value is
 *
 *            TRUE -> Selected Ui Mode is Dark
 *
 *            FALSE -> Selected Ui Mode is Light
 *
 *            NULL -> Selected Ui Mode is System Theme
 *
 * This variable is public because it is used in system settings */
lateinit var isUiModeIsDark : MutableState<Boolean?>

@Composable
fun MusicBrainzTheme(
    systemUiController : SystemUiController = rememberSystemUiController(),
    systemTheme : Boolean = isSystemInDarkTheme(),
    context: Context = LocalContext.current,
    content: @Composable () -> Unit
) {
    isUiModeIsDark = remember { mutableStateOf(userSelectedThemeIsNight(context)) }
    val colorScheme = when (isUiModeIsDark.value) {
        true -> DarkColorScheme
        false -> LightColorScheme
        else -> if (systemTheme) DarkColorScheme else LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.background.toArgb()
            val isDark = when (isUiModeIsDark.value){
                true -> false
                false -> true
                else -> !systemTheme
            }
            systemUiController.statusBarDarkContentEnabled = isDark
            systemUiController.navigationBarDarkContentEnabled = isDark
            systemUiController.setNavigationBarColor(color = colorScheme.background)
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}



fun userSelectedThemeIsNight(context: Context) : Boolean? {
    return when (PreferenceManager.getDefaultSharedPreferences(context)
        .getString("app_theme", "Use device theme")){   // R.string.settings_device_theme_use_device_theme
        "Dark" -> true
        "Light" -> false
        else -> null
    }
}
