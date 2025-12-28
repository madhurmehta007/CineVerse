package com.android.cineverse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import com.android.cineverse.ui.components.AnimatedPressButton
import com.android.cineverse.ui.components.CineVerseAnimation
import com.android.cineverse.ui.components.LocalAnimatedVisibilityScope
import com.android.cineverse.ui.components.LocalSharedTransitionScope
import com.android.cineverse.ui.theme.AccentPink
import com.android.cineverse.ui.theme.CardBackground
import com.android.cineverse.ui.theme.DarkPurple
import com.android.cineverse.ui.theme.GradientEnd
import com.android.cineverse.ui.theme.GradientStart
import com.android.cineverse.ui.viewmodel.AndroidMoviesViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MovieDetailScreen(
    movieId: String,
    onBackClick: () -> Unit,
    viewModel: AndroidMoviesViewModel = koinViewModel()
) {
    val allMovies by viewModel.movies.collectAsState()
    val movie = allMovies.find { it.id == movieId }
    
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedVisibilityScope.current

    // Animation states for staggered reveal
    var showPlayButton by remember { mutableStateOf(false) }
    var showSynopsis by remember { mutableStateOf(false) }
    var showDirector by remember { mutableStateOf(false) }
    var showCast by remember { mutableStateOf(false) }

    // Trigger staggered animations on launch
    LaunchedEffect(Unit) {
        delay(200) // Wait for shared element transition
        showPlayButton = true
        delay(CineVerseAnimation.STAGGER_DELAY.toLong())
        showSynopsis = true
        delay(CineVerseAnimation.STAGGER_DELAY.toLong())
        showDirector = true
        delay(CineVerseAnimation.STAGGER_DELAY.toLong())
        showCast = true
    }

    if (movie != null) {
        Scaffold(
            containerColor = DarkPurple
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp)
            ) {
                // Header Image with shared element transition
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    // Shared element poster
                    val posterModifier = if (sharedTransitionScope != null && animatedContentScope != null) {
                        with(sharedTransitionScope) {
                            Modifier
                                .fillMaxSize()
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = "poster-${movie.id}"),
                                    animatedVisibilityScope = animatedContentScope
                                )
                        }
                    } else {
                        Modifier.fillMaxSize()
                    }
                    
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = posterModifier
                    )
                    
                    // Gradient Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, DarkPurple),
                                    startY = 300f
                                )
                            )
                    )

                    // Toolbar overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }

                        IconButton(
                            onClick = { viewModel.toggleFavorite(movieId) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = if (movie.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (movie.isFavorite) AccentPink else Color.White
                            )
                        }
                    }
                    
                    // Rating Element
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp, bottom = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = movie.rating.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Info Section
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = movie.releaseDate.take(4), color = Color.LightGray)
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = movie.duration, color = Color.LightGray)
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Genre bubbles
                        FlowRow(
                           horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                             movie.genres.forEach { genre ->
                                 Surface(
                                     color = CardBackground,
                                     shape = RoundedCornerShape(16.dp)
                                 ) {
                                     Text(
                                         text = genre,
                                         modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                         style = MaterialTheme.typography.labelSmall,
                                         color = Color.White
                                     )
                                 }
                             }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
            
                    // Play Button with press animation
                    AnimatedVisibility(
                        visible = showPlayButton,
                        enter = fadeIn(tween(CineVerseAnimation.STANDARD)) + 
                                slideInVertically(tween(CineVerseAnimation.STANDARD)) { it / 3 }
                    ) {
                        AnimatedPressButton(
                            onClick = { /* Play action */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(listOf(GradientStart, GradientEnd)),
                                        shape = RoundedCornerShape(28.dp)
                                    )
                                    .clip(RoundedCornerShape(28.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Play Now", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Staggered Detail Sections
                    AnimatedDetailSection(
                        title = "Synopsis", 
                        content = movie.synopsis,
                        visible = showSynopsis
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AnimatedDetailSection(
                        title = "Director", 
                        content = movie.director,
                        visible = showDirector
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AnimatedDetailSection(
                        title = "Cast", 
                        content = movie.cast.joinToString(", "),
                        visible = showCast
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedDetailSection(
    title: String, 
    content: String,
    visible: Boolean
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(CineVerseAnimation.STANDARD)) + 
                slideInVertically(tween(CineVerseAnimation.STANDARD)) { it / 4 }
    ) {
        Surface(
            color = CardBackground,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = title, 
                    style = MaterialTheme.typography.titleMedium, 
                    color = Color.White, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = content, 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = Color.LightGray, 
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
            }
        }
    }
}

@Composable
fun DetailSection(title: String, content: String) {
    Surface(
        color = CardBackground,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = content, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray, lineHeight = MaterialTheme.typography.bodyLarge.lineHeight)
        }
    }
}
