package com.android.cineverse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.cineverse.ui.components.AnimatedPressButton
import com.android.cineverse.ui.components.LocalAnimatedVisibilityScope
import com.android.cineverse.ui.components.LocalSharedTransitionScope
import com.android.cineverse.ui.theme.AccentPink
import com.android.cineverse.ui.theme.CardBackground
import com.android.cineverse.ui.theme.DarkPurple
import com.android.cineverse.ui.theme.GradientEnd
import com.android.cineverse.ui.theme.GradientStart
import com.android.cineverse.ui.viewmodel.AndroidMoviesViewModel
import kotlinx.coroutines.delay
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

    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(550)
        showContent = true
    }

    if (movie != null) {
        Scaffold(
            containerColor = DarkPurple
        ) { padding ->
            if (sharedTransitionScope != null && animatedContentScope != null) {
                with(sharedTransitionScope) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .sharedBounds(
                                rememberSharedContentState(key = "container-${movie.id}"),
                                animatedVisibilityScope = animatedContentScope,
                                enter = fadeIn(),
                                exit = fadeOut()
                            )
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Full-screen poster image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(600.dp)
                        ) {
                            AsyncImage(
                                model = movie.posterUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .sharedElement(
                                        rememberSharedContentState(key = "poster-${movie.id}"),
                                        animatedVisibilityScope = animatedContentScope
                                    )
                            )

                            // Dark gradient at bottom for card overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, DarkPurple),
                                            startY = 400f
                                        )
                                    )
                            )

                            // Toolbar overlay at top
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

                            // Rating badge in top-left
                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(start = 16.dp, top = 120.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = movie.rating.toString(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }

                            // Bottom card with movie info
                            AnimatedVisibility(
                                visible = showContent,
                                modifier = Modifier.align(Alignment.BottomCenter),
                                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 3 }
                            ) {
                                Surface(
                                    color = CardBackground,
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .padding(bottom = 28.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        // Title
                                        Text(
                                            text = movie.title,
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Metadata row: year, duration, genre
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            // Year
                                            Text(
                                                text = movie.releaseDate.take(4),
                                                color = Color.LightGray,
                                                fontSize = 14.sp
                                            )

                                            // Dot separator
                                            Text(
                                                text = "•",
                                                color = Color.Gray,
                                                fontSize = 14.sp
                                            )

                                            // Duration
                                            Text(
                                                text = movie.duration,
                                                color = Color.LightGray,
                                                fontSize = 14.sp
                                            )

                                            // Genre tag
                                            movie.genres.firstOrNull()?.let { genre ->
                                                Surface(
                                                    color = AccentPink.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(20.dp)
                                                ) {
                                                    Text(
                                                        text = genre,
                                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = AccentPink,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        // Play Now button with pink gradient
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
                                                        brush = Brush.horizontalGradient(
                                                            listOf(GradientStart, GradientEnd)
                                                        ),
                                                        shape = RoundedCornerShape(28.dp)
                                                    )
                                                    .clip(RoundedCornerShape(28.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        Icons.Default.PlayArrow,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(28.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        "Play Now",
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontSize = 18.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Synopsis section below the image
                        Column(
                            modifier = Modifier
                                .padding(top = 620.dp)
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 32.dp)
                        ) {
                            AnimatedVisibility(
                                visible = showContent,
                                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 4 }
                            ) {
                                Surface(
                                    color = CardBackground,
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(24.dp)) {
                                        Text(
                                            text = "Synopsis",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = movie.synopsis,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.LightGray,
                                            lineHeight = 22.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Director section
                            AnimatedVisibility(
                                visible = showContent,
                                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 4 }
                            ) {
                                Surface(
                                    color = CardBackground,
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(24.dp)) {
                                        Text(
                                            text = "Director",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = movie.director,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Cast section
                            AnimatedVisibility(
                                visible = showContent,
                                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 4 }
                            ) {
                                Surface(
                                    color = CardBackground,
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(24.dp)) {
                                        Text(
                                            text = "Cast",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = movie.cast.joinToString(", "),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Fallback for when scopes are null
                DetailScreenContent(movie = movie, onBackClick = onBackClick, viewModel = viewModel)
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Movie not found", color = Color.White)
        }
    }
}

@Composable
private fun DetailScreenContent(
    movie: org.android.cineverse.shared.domain.model.Movie,
    onBackClick: () -> Unit,
    viewModel: AndroidMoviesViewModel
) {
    // Fallback implementation
    Scaffold(
        containerColor = DarkPurple
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
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
                        onClick = { viewModel.toggleFavorite(movie.id) },
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
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Surface(
                    color = CardBackground,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Synopsis",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = movie.synopsis,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                        )
                    }
                }
            }
        }
    }
}