package com.example.compose
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.monefy.presentation.theme.AppTypography
import com.example.monefy.presentation.theme.backgroundDark
import com.example.monefy.presentation.theme.backgroundDarkHighContrast
import com.example.monefy.presentation.theme.backgroundDarkMediumContrast
import com.example.monefy.presentation.theme.backgroundLight
import com.example.monefy.presentation.theme.backgroundLightHighContrast
import com.example.monefy.presentation.theme.backgroundLightMediumContrast
import com.example.monefy.presentation.theme.errorContainerDark
import com.example.monefy.presentation.theme.errorContainerDarkHighContrast
import com.example.monefy.presentation.theme.errorContainerDarkMediumContrast
import com.example.monefy.presentation.theme.errorContainerLight
import com.example.monefy.presentation.theme.errorContainerLightHighContrast
import com.example.monefy.presentation.theme.errorContainerLightMediumContrast
import com.example.monefy.presentation.theme.errorDark
import com.example.monefy.presentation.theme.errorDarkHighContrast
import com.example.monefy.presentation.theme.errorDarkMediumContrast
import com.example.monefy.presentation.theme.errorLight
import com.example.monefy.presentation.theme.errorLightHighContrast
import com.example.monefy.presentation.theme.errorLightMediumContrast
import com.example.monefy.presentation.theme.inverseOnSurfaceDark
import com.example.monefy.presentation.theme.inverseOnSurfaceDarkHighContrast
import com.example.monefy.presentation.theme.inverseOnSurfaceDarkMediumContrast
import com.example.monefy.presentation.theme.inverseOnSurfaceLight
import com.example.monefy.presentation.theme.inverseOnSurfaceLightHighContrast
import com.example.monefy.presentation.theme.inverseOnSurfaceLightMediumContrast
import com.example.monefy.presentation.theme.inversePrimaryDark
import com.example.monefy.presentation.theme.inversePrimaryDarkHighContrast
import com.example.monefy.presentation.theme.inversePrimaryDarkMediumContrast
import com.example.monefy.presentation.theme.inversePrimaryLight
import com.example.monefy.presentation.theme.inversePrimaryLightHighContrast
import com.example.monefy.presentation.theme.inversePrimaryLightMediumContrast
import com.example.monefy.presentation.theme.inverseSurfaceDark
import com.example.monefy.presentation.theme.inverseSurfaceDarkHighContrast
import com.example.monefy.presentation.theme.inverseSurfaceDarkMediumContrast
import com.example.monefy.presentation.theme.inverseSurfaceLight
import com.example.monefy.presentation.theme.inverseSurfaceLightHighContrast
import com.example.monefy.presentation.theme.inverseSurfaceLightMediumContrast
import com.example.monefy.presentation.theme.onBackgroundDark
import com.example.monefy.presentation.theme.onBackgroundDarkHighContrast
import com.example.monefy.presentation.theme.onBackgroundDarkMediumContrast
import com.example.monefy.presentation.theme.onBackgroundLight
import com.example.monefy.presentation.theme.onBackgroundLightHighContrast
import com.example.monefy.presentation.theme.onBackgroundLightMediumContrast
import com.example.monefy.presentation.theme.onErrorContainerDark
import com.example.monefy.presentation.theme.onErrorContainerDarkHighContrast
import com.example.monefy.presentation.theme.onErrorContainerDarkMediumContrast
import com.example.monefy.presentation.theme.onErrorContainerLight
import com.example.monefy.presentation.theme.onErrorContainerLightHighContrast
import com.example.monefy.presentation.theme.onErrorContainerLightMediumContrast
import com.example.monefy.presentation.theme.onErrorDark
import com.example.monefy.presentation.theme.onErrorDarkHighContrast
import com.example.monefy.presentation.theme.onErrorDarkMediumContrast
import com.example.monefy.presentation.theme.onErrorLight
import com.example.monefy.presentation.theme.onErrorLightHighContrast
import com.example.monefy.presentation.theme.onErrorLightMediumContrast
import com.example.monefy.presentation.theme.onPrimaryContainerDark
import com.example.monefy.presentation.theme.onPrimaryContainerDarkHighContrast
import com.example.monefy.presentation.theme.onPrimaryContainerDarkMediumContrast
import com.example.monefy.presentation.theme.onPrimaryContainerLight
import com.example.monefy.presentation.theme.onPrimaryContainerLightHighContrast
import com.example.monefy.presentation.theme.onPrimaryContainerLightMediumContrast
import com.example.monefy.presentation.theme.onPrimaryDark
import com.example.monefy.presentation.theme.onPrimaryDarkHighContrast
import com.example.monefy.presentation.theme.onPrimaryDarkMediumContrast
import com.example.monefy.presentation.theme.onPrimaryLight
import com.example.monefy.presentation.theme.onPrimaryLightHighContrast
import com.example.monefy.presentation.theme.onPrimaryLightMediumContrast
import com.example.monefy.presentation.theme.onSecondaryContainerDark
import com.example.monefy.presentation.theme.onSecondaryContainerDarkHighContrast
import com.example.monefy.presentation.theme.onSecondaryContainerDarkMediumContrast
import com.example.monefy.presentation.theme.onSecondaryContainerLight
import com.example.monefy.presentation.theme.onSecondaryContainerLightHighContrast
import com.example.monefy.presentation.theme.onSecondaryContainerLightMediumContrast
import com.example.monefy.presentation.theme.onSecondaryDark
import com.example.monefy.presentation.theme.onSecondaryDarkHighContrast
import com.example.monefy.presentation.theme.onSecondaryDarkMediumContrast
import com.example.monefy.presentation.theme.onSecondaryLight
import com.example.monefy.presentation.theme.onSecondaryLightHighContrast
import com.example.monefy.presentation.theme.onSecondaryLightMediumContrast
import com.example.monefy.presentation.theme.onSurfaceDark
import com.example.monefy.presentation.theme.onSurfaceDarkHighContrast
import com.example.monefy.presentation.theme.onSurfaceDarkMediumContrast
import com.example.monefy.presentation.theme.onSurfaceLight
import com.example.monefy.presentation.theme.onSurfaceLightHighContrast
import com.example.monefy.presentation.theme.onSurfaceLightMediumContrast
import com.example.monefy.presentation.theme.onSurfaceVariantDark
import com.example.monefy.presentation.theme.onSurfaceVariantDarkHighContrast
import com.example.monefy.presentation.theme.onSurfaceVariantDarkMediumContrast
import com.example.monefy.presentation.theme.onSurfaceVariantLight
import com.example.monefy.presentation.theme.onSurfaceVariantLightHighContrast
import com.example.monefy.presentation.theme.onSurfaceVariantLightMediumContrast
import com.example.monefy.presentation.theme.onTertiaryContainerDark
import com.example.monefy.presentation.theme.onTertiaryContainerDarkHighContrast
import com.example.monefy.presentation.theme.onTertiaryContainerDarkMediumContrast
import com.example.monefy.presentation.theme.onTertiaryContainerLight
import com.example.monefy.presentation.theme.onTertiaryContainerLightHighContrast
import com.example.monefy.presentation.theme.onTertiaryContainerLightMediumContrast
import com.example.monefy.presentation.theme.onTertiaryDark
import com.example.monefy.presentation.theme.onTertiaryDarkHighContrast
import com.example.monefy.presentation.theme.onTertiaryDarkMediumContrast
import com.example.monefy.presentation.theme.onTertiaryLight
import com.example.monefy.presentation.theme.onTertiaryLightHighContrast
import com.example.monefy.presentation.theme.onTertiaryLightMediumContrast
import com.example.monefy.presentation.theme.outlineDark
import com.example.monefy.presentation.theme.outlineDarkHighContrast
import com.example.monefy.presentation.theme.outlineDarkMediumContrast
import com.example.monefy.presentation.theme.outlineLight
import com.example.monefy.presentation.theme.outlineLightHighContrast
import com.example.monefy.presentation.theme.outlineLightMediumContrast
import com.example.monefy.presentation.theme.outlineVariantDark
import com.example.monefy.presentation.theme.outlineVariantDarkHighContrast
import com.example.monefy.presentation.theme.outlineVariantDarkMediumContrast
import com.example.monefy.presentation.theme.outlineVariantLight
import com.example.monefy.presentation.theme.outlineVariantLightHighContrast
import com.example.monefy.presentation.theme.outlineVariantLightMediumContrast
import com.example.monefy.presentation.theme.primaryContainerDark
import com.example.monefy.presentation.theme.primaryContainerDarkHighContrast
import com.example.monefy.presentation.theme.primaryContainerDarkMediumContrast
import com.example.monefy.presentation.theme.primaryContainerLight
import com.example.monefy.presentation.theme.primaryContainerLightHighContrast
import com.example.monefy.presentation.theme.primaryContainerLightMediumContrast
import com.example.monefy.presentation.theme.primaryDark
import com.example.monefy.presentation.theme.primaryDarkHighContrast
import com.example.monefy.presentation.theme.primaryDarkMediumContrast
import com.example.monefy.presentation.theme.primaryLight
import com.example.monefy.presentation.theme.primaryLightHighContrast
import com.example.monefy.presentation.theme.primaryLightMediumContrast
import com.example.monefy.presentation.theme.scrimDark
import com.example.monefy.presentation.theme.scrimDarkHighContrast
import com.example.monefy.presentation.theme.scrimDarkMediumContrast
import com.example.monefy.presentation.theme.scrimLight
import com.example.monefy.presentation.theme.scrimLightHighContrast
import com.example.monefy.presentation.theme.scrimLightMediumContrast
import com.example.monefy.presentation.theme.secondaryContainerDark
import com.example.monefy.presentation.theme.secondaryContainerDarkHighContrast
import com.example.monefy.presentation.theme.secondaryContainerDarkMediumContrast
import com.example.monefy.presentation.theme.secondaryContainerLight
import com.example.monefy.presentation.theme.secondaryContainerLightHighContrast
import com.example.monefy.presentation.theme.secondaryContainerLightMediumContrast
import com.example.monefy.presentation.theme.secondaryDark
import com.example.monefy.presentation.theme.secondaryDarkHighContrast
import com.example.monefy.presentation.theme.secondaryDarkMediumContrast
import com.example.monefy.presentation.theme.secondaryLight
import com.example.monefy.presentation.theme.secondaryLightHighContrast
import com.example.monefy.presentation.theme.secondaryLightMediumContrast
import com.example.monefy.presentation.theme.surfaceBrightDark
import com.example.monefy.presentation.theme.surfaceBrightDarkHighContrast
import com.example.monefy.presentation.theme.surfaceBrightDarkMediumContrast
import com.example.monefy.presentation.theme.surfaceBrightLight
import com.example.monefy.presentation.theme.surfaceBrightLightHighContrast
import com.example.monefy.presentation.theme.surfaceBrightLightMediumContrast
import com.example.monefy.presentation.theme.surfaceContainerDark
import com.example.monefy.presentation.theme.surfaceContainerDarkHighContrast
import com.example.monefy.presentation.theme.surfaceContainerDarkMediumContrast
import com.example.monefy.presentation.theme.surfaceContainerHighDark
import com.example.monefy.presentation.theme.surfaceContainerHighDarkHighContrast
import com.example.monefy.presentation.theme.surfaceContainerHighDarkMediumContrast
import com.example.monefy.presentation.theme.surfaceContainerHighLight
import com.example.monefy.presentation.theme.surfaceContainerHighLightHighContrast
import com.example.monefy.presentation.theme.surfaceContainerHighLightMediumContrast
import com.example.monefy.presentation.theme.surfaceContainerHighestDark
import com.example.monefy.presentation.theme.surfaceContainerHighestDarkHighContrast
import com.example.monefy.presentation.theme.surfaceContainerHighestDarkMediumContrast
import com.example.monefy.presentation.theme.surfaceContainerHighestLight
import com.example.monefy.presentation.theme.surfaceContainerHighestLightHighContrast
import com.example.monefy.presentation.theme.surfaceContainerHighestLightMediumContrast
import com.example.monefy.presentation.theme.surfaceContainerLight
import com.example.monefy.presentation.theme.surfaceContainerLightHighContrast
import com.example.monefy.presentation.theme.surfaceContainerLightMediumContrast
import com.example.monefy.presentation.theme.surfaceContainerLowDark
import com.example.monefy.presentation.theme.surfaceContainerLowDarkHighContrast
import com.example.monefy.presentation.theme.surfaceContainerLowDarkMediumContrast
import com.example.monefy.presentation.theme.surfaceContainerLowLight
import com.example.monefy.presentation.theme.surfaceContainerLowLightHighContrast
import com.example.monefy.presentation.theme.surfaceContainerLowLightMediumContrast
import com.example.monefy.presentation.theme.surfaceContainerLowestDark
import com.example.monefy.presentation.theme.surfaceContainerLowestDarkHighContrast
import com.example.monefy.presentation.theme.surfaceContainerLowestDarkMediumContrast
import com.example.monefy.presentation.theme.surfaceContainerLowestLight
import com.example.monefy.presentation.theme.surfaceContainerLowestLightHighContrast
import com.example.monefy.presentation.theme.surfaceContainerLowestLightMediumContrast
import com.example.monefy.presentation.theme.surfaceDark
import com.example.monefy.presentation.theme.surfaceDarkHighContrast
import com.example.monefy.presentation.theme.surfaceDarkMediumContrast
import com.example.monefy.presentation.theme.surfaceDimDark
import com.example.monefy.presentation.theme.surfaceDimDarkHighContrast
import com.example.monefy.presentation.theme.surfaceDimDarkMediumContrast
import com.example.monefy.presentation.theme.surfaceDimLight
import com.example.monefy.presentation.theme.surfaceDimLightHighContrast
import com.example.monefy.presentation.theme.surfaceDimLightMediumContrast
import com.example.monefy.presentation.theme.surfaceLight
import com.example.monefy.presentation.theme.surfaceLightHighContrast
import com.example.monefy.presentation.theme.surfaceLightMediumContrast
import com.example.monefy.presentation.theme.surfaceVariantDark
import com.example.monefy.presentation.theme.surfaceVariantDarkHighContrast
import com.example.monefy.presentation.theme.surfaceVariantDarkMediumContrast
import com.example.monefy.presentation.theme.surfaceVariantLight
import com.example.monefy.presentation.theme.surfaceVariantLightHighContrast
import com.example.monefy.presentation.theme.surfaceVariantLightMediumContrast
import com.example.monefy.presentation.theme.tertiaryContainerDark
import com.example.monefy.presentation.theme.tertiaryContainerDarkHighContrast
import com.example.monefy.presentation.theme.tertiaryContainerDarkMediumContrast
import com.example.monefy.presentation.theme.tertiaryContainerLight
import com.example.monefy.presentation.theme.tertiaryContainerLightHighContrast
import com.example.monefy.presentation.theme.tertiaryContainerLightMediumContrast
import com.example.monefy.presentation.theme.tertiaryDark
import com.example.monefy.presentation.theme.tertiaryDarkHighContrast
import com.example.monefy.presentation.theme.tertiaryDarkMediumContrast
import com.example.monefy.presentation.theme.tertiaryLight
import com.example.monefy.presentation.theme.tertiaryLightHighContrast
import com.example.monefy.presentation.theme.tertiaryLightMediumContrast

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun AppTheme(
    darkTheme: Boolean = true,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
) {
  val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      
      darkTheme -> darkScheme
      else -> lightScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}

