package org.android.cineverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.cineverse.ui.screens.FavoritesScreen
import com.android.cineverse.ui.screens.MovieDetailScreen
import com.android.cineverse.ui.screens.MoviesGridScreen
import org.android.cineverse.ui.theme.CineVerseTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CineVerseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    SharedTransitionLayout {
                        CompositionLocalProvider(
                            LocalSharedTransitionScope provides this
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = "movies"
                            ) {
                                composable("movies") {
                                    CompositionLocalProvider(
                                        LocalAnimatedVisibilityScope provides this
                                    ) {
                                        MoviesGridScreen(
                                            onMovieClick = { movieId ->
                                                navController.navigate("movie/$movieId")
                                            },
                                            onFavoritesClick = {
                                                navController.navigate("favorites")
                                            }
                                        )
                                    }
                                }
                                
                                composable(
                                    route = "movie/{movieId}",
                                    arguments = listOf(
                                        navArgument("movieId") { type = NavType.StringType }
                                    )
                                ) { backStackEntry ->
                                    CompositionLocalProvider(
                                        LocalAnimatedVisibilityScope provides this
                                    ) {
                                        backStackEntry.arguments?.getString("movieId")?.let { movieId ->
                                            MovieDetailScreen(
                                                movieId = movieId,
                                                onBackClick = { navController.popBackStack() }
                                            )
                                        }
                                    }
                                }
                                
                                composable("favorites") {
                                    CompositionLocalProvider(
                                        LocalAnimatedVisibilityScope provides this
                                    ) {
                                        FavoritesScreen(
                                            onMovieClick = { movieId ->
                                                navController.navigate("movie/$movieId")
                                            },
                                            onBackClick = { navController.popBackStack() }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
