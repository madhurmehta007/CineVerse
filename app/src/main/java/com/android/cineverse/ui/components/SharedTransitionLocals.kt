package com.android.cineverse.ui.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocals for shared element transitions.
 * These provide the animation scopes needed for shared element animations
 * with Navigation Compose.
 * 
 * LocalAnimatedVisibilityScope - Provides AnimatedContentScope from NavHost composable
 * LocalSharedTransitionScope - Provides SharedTransitionScope from SharedTransitionLayout
 */
val LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedContentScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
