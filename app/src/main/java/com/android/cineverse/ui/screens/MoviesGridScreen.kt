package com.android.cineverse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.cineverse.ui.components.MovieCard
import com.android.cineverse.ui.viewmodel.AndroidMoviesViewModel
import com.android.cineverse.ui.theme.AccentPink
import com.android.cineverse.ui.theme.CardBackground
import com.android.cineverse.ui.theme.DarkPurple
import org.koin.androidx.compose.koinViewModel

@Composable
fun MoviesGridScreen(
    onMovieClick: (String) -> Unit,
    onFavoritesClick: () -> Unit,
    viewModel: AndroidMoviesViewModel = koinViewModel()
) {
    val movies by viewModel.movies.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkPurple)
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Movies",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    IconButton(
                        onClick = onFavoritesClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBackground)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = AccentPink
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Search movies...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = 16.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(movies, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie,
                        onMovieClick = onMovieClick,
                        onFavoriteClick = { viewModel.toggleFavorite(it) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
            
            if (movies.isEmpty()) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "No movies found" else "Loading...",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

