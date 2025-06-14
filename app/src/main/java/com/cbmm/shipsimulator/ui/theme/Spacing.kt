package com.cbmm.shipsimulator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp

/**
 * Spacing values used throughout the app.
 * Follows the 4dp grid system.
 */
object Spacing {
    val none = 0.dp
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
    val extraExtraLarge = 48.dp
}

/**
 * Spacing extension for MaterialTheme.
 * This allows accessing spacing values via MaterialTheme.spacing
 */
val MaterialTheme.spacing: Spacing
    get() = Spacing
