// SPDX-License-Identifier: GPL-3.0-only

package app.tellyscope.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Branded fallback for phones without dynamic color (Android 11 and older).
// Seed: a deep teal, picked to read as "screen glow" without clashing with app icon tiles.
// Every role is set, tonal-spot tones from that seed: a role left out falls back to
// Material's stock purple.
private val LightBrand = lightColorScheme(
    primary = Color(0xFFB51232),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFA0AD),
    onPrimaryContainer = Color(0xFF002020),
    inversePrimary = Color(0xFFFF526B),
    secondary = Color(0xFF4A6363),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD7DE),
    onSecondaryContainer = Color(0xFF051F1F),
    tertiary = Color(0xFF4B607C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD2DB),
    onTertiaryContainer = Color(0xFF041C35),
    background = Color(0xFFF4FBFA),
    onBackground = Color(0xFF12090D),
    surface = Color(0xFFF4FBFA),
    onSurface = Color(0xFF12090D),
    surfaceVariant = Color(0xFFDAE4E4),
    onSurfaceVariant = Color(0xFF51212D),
    inverseSurface = Color(0xFF2B3232),
    inverseOnSurface = Color(0xFFECF2F1),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF6F7979),
    outlineVariant = Color(0xFFD7AEB6),
    surfaceBright = Color(0xFFF4FBFA),
    surfaceContainer = Color(0xFFE9EFEE),
    surfaceContainerHigh = Color(0xFFE3E9E9),
    surfaceContainerHighest = Color(0xFFFFE9ED),
    surfaceContainerLow = Color(0xFFEEF5F4),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFD5DBDB),
    primaryFixed = Color(0xFFFFA0AD),
    primaryFixedDim = Color(0xFFFF526B),
    onPrimaryFixed = Color(0xFF002020),
    onPrimaryFixedVariant = Color(0xFF650F22),
    secondaryFixed = Color(0xFFCCE8E7),
    secondaryFixedDim = Color(0xFFE8AAB4),
    onSecondaryFixed = Color(0xFF051F1F),
    onSecondaryFixedVariant = Color(0xFF4B1824),
    tertiaryFixed = Color(0xFFFFD2DB),
    tertiaryFixedDim = Color(0xFFE7A0AE),
    onTertiaryFixed = Color(0xFF041C35),
    onTertiaryFixedVariant = Color(0xFF571C2A),
)

private val DarkBrand = darkColorScheme(
    primary = Color(0xFFFF526B),
    onPrimary = Color(0xFF24070D),
    primaryContainer = Color(0xFF650F22),
    onPrimaryContainer = Color(0xFFFFA0AD),
    inversePrimary = Color(0xFFB51232),
    secondary = Color(0xFFE8AAB4),
    onSecondary = Color(0xFF2A0C13),
    secondaryContainer = Color(0xFF4B1824),
    onSecondaryContainer = Color(0xFFFFD7DE),
    tertiary = Color(0xFFE7A0AE),
    onTertiary = Color(0xFF32101A),
    tertiaryContainer = Color(0xFF571C2A),
    onTertiaryContainer = Color(0xFFFFD2DB),
    background = Color(0xFF0B0609),
    onBackground = Color(0xFFFFE9ED),
    surface = Color(0xFF0B0609),
    onSurface = Color(0xFFFFE9ED),
    surfaceVariant = Color(0xFF51212D),
    onSurfaceVariant = Color(0xFFD7AEB6),
    inverseSurface = Color(0xFFFFE9ED),
    inverseOnSurface = Color(0xFF2B3232),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFFB97886),
    outlineVariant = Color(0xFF51212D),
    surfaceBright = Color(0xFF3B101B),
    surfaceContainer = Color(0xFF180A0F),
    surfaceContainerHigh = Color(0xFF241017),
    surfaceContainerHighest = Color(0xFF32141D),
    surfaceContainerLow = Color(0xFF12090D),
    surfaceContainerLowest = Color(0xFF070406),
    surfaceDim = Color(0xFF0B0609),
    primaryFixed = Color(0xFFFFA0AD),
    primaryFixedDim = Color(0xFFFF526B),
    onPrimaryFixed = Color(0xFF002020),
    onPrimaryFixedVariant = Color(0xFF650F22),
    secondaryFixed = Color(0xFFCCE8E7),
    secondaryFixedDim = Color(0xFFE8AAB4),
    onSecondaryFixed = Color(0xFF051F1F),
    onSecondaryFixedVariant = Color(0xFF4B1824),
    tertiaryFixed = Color(0xFFFFD2DB),
    tertiaryFixedDim = Color(0xFFE7A0AE),
    onTertiaryFixed = Color(0xFF041C35),
    onTertiaryFixedVariant = Color(0xFF571C2A),
)

/** Numbers come first: the app font with tabular figures, so values line up and don't jitter as they update. */
val StatNumber = TextStyle(fontWeight = FontWeight.Medium, fontSize = 28.sp, lineHeight = 34.sp, fontFeatureSettings = "tnum")
val SmallNumber = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontFeatureSettings = "tnum")

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TellyscopeTheme(
    dark: Boolean = true,
    dynamic: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colors = when {
        dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> DarkBrand
        else -> LightBrand
    }
    MaterialExpressiveTheme(
        colorScheme = colors,
        motionScheme = MotionScheme.expressive(),
        typography = Typography(),
        content = content,
    )
}
