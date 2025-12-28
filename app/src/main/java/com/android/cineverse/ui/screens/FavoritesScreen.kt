package com.android.cineverse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.cineverse.ui.components.MovieCard
import com.android.cineverse.ui.theme.DarkPurple
import com.android.cineverse.ui.viewmodel.AndroidMoviesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    onMovieClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: AndroidMoviesViewModel = koinViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        topBar = {
             Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkPurple)
                    .padding(16.dp, 40.dp, 16.dp, 16.dp)
            ) {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.1f))
                     ) {
                         Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                     }
                     Text(
                        text = "Favorites",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                 }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favorites yet", color = Color.Gray)
            }
        } else {
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
                items(favorites, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie,
                        onMovieClick = onMovieClick,
                        onFavoriteClick = { viewModel.toggleFavorite(it) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}
