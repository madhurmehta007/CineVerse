package com.android.cineverse.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.cineverse.ui.theme.AccentPink
import kotlinx.coroutines.delay
import org.android.cineverse.shared.domain.model.Movie

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MovieCard(
    movie: Movie,
    onMovieClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    var showOverlay by remember { mutableStateOf(false) }
    
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedVisibilityScope.current
    
    // Auto-hide overlay after delay
    LaunchedEffect(showOverlay) {
        if (showOverlay) {
            delay(2000)
            showOverlay = false
            isPressed = false  // Reset lift effect when overlay hides
        }
    }
    
    // Animate card lift effect
    val scale by animateFloatAsState(
        targetValue = if (isPressed) CineVerseAnimation.CARD_LIFT_SCALE else 1f,
        animationSpec = CineVerseAnimation.gentleSpring(),
        label = "card_scale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 12.dp else 4.dp,
        animationSpec = CineVerseAnimation.quickTween(),
        label = "card_elevation"
    )
    
    val translationY by animateFloatAsState(
        targetValue = if (isPressed) -8f else 0f,
        animationSpec = CineVerseAnimation.gentleSpring(),
        label = "card_translation"
    )
    
    // Animate overlay visibility
    val overlayAlpha by animateFloatAsState(
        targetValue = if (showOverlay) 1f else 0f,
        animationSpec = CineVerseAnimation.quickTween(),
        label = "overlay_alpha"
    )
    
    val overlayTranslation by animateFloatAsState(
        targetValue = if (showOverlay) 0f else 30f,
        animationSpec = CineVerseAnimation.gentleSpring(),
        label = "overlay_translation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.translationY = translationY
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onMovieClick(movie.id) },
                    onLongPress = { 
                        // Only trigger animation on long press
                        isPressed = true
                        showOverlay = true
                    }
                )
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Movie Poster with shared element
            val posterModifier = if (sharedTransitionScope != null && animatedContentScope != null) {
                with(sharedTransitionScope) {
                    imageModifier
                        .fillMaxSize()
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "poster-${movie.id}"),
                            animatedVisibilityScope = animatedContentScope
                        )
                }
            } else {
                imageModifier.fillMaxSize()
            }
            
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = posterModifier.clip(RoundedCornerShape(16.dp))
            )

            // Always-visible bottom gradient with title
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                            startY = 250f
                        )
                    )
            )

            // Basic title (fades out when overlay shows)
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                    .alpha(1f - overlayAlpha)
            )

            // Animated info overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .alpha(overlayAlpha)
                    .graphicsLayer { this.translationY = overlayTranslation }
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f)),
                            startY = 0f
                        )
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = movie.releaseDate.take(4),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.Yellow,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = movie.rating.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
            }

            // Favorite button
            IconButton(
                onClick = { onFavoriteClick(movie.id) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (movie.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (movie.isFavorite) AccentPink else Color.White
                )
            }
        }
    }
}
