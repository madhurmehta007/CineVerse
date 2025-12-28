package com.android.cineverse.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Animation specifications for consistent motion design across the app.
 * Based on Figma design specifications.
 */
object CineVerseAnimation {
    
    // Durations
    const val QUICK = 150
    const val STANDARD = 300
    const val EMPHASIZED = 400
    
    // Card animations
    const val CARD_LIFT_SCALE = 1.03f
    const val CARD_PRESS_SCALE = 0.97f
    
    // Button animations
    const val BUTTON_PRESS_SCALE = 0.95f
    const val BUTTON_PRESS_ALPHA = 0.8f
    
    // Stagger delays for list items
    const val STAGGER_DELAY = 100
    
    // Spring specs
    fun <T> gentleSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    fun <T> quickSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    // Tween specs
    fun <T> standardTween() = tween<T>(
        durationMillis = STANDARD,
        easing = FastOutSlowInEasing
    )
    
    fun <T> quickTween() = tween<T>(
        durationMillis = QUICK,
        easing = FastOutSlowInEasing
    )
}
