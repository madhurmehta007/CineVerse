package com.android.cineverse.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * A button wrapper that provides scale and alpha feedback on press.
 * Matches Figma design specifications for glassmorphic button feedback.
 */
@Composable
fun AnimatedPressButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) CineVerseAnimation.BUTTON_PRESS_SCALE else 1f,
        animationSpec = CineVerseAnimation.quickSpring(),
        label = "button_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) CineVerseAnimation.BUTTON_PRESS_ALPHA else 1f,
        animationSpec = CineVerseAnimation.quickTween(),
        label = "button_alpha"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        content = content
    )
}
