package com.android.cineverse.data

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.android.cineverse.shared.data.local.FavoritesLocalDataSource
import org.android.cineverse.shared.data.remote.MovieRemoteDataSource
import org.android.cineverse.shared.data.repository.MovieRepository
import org.android.cineverse.shared.domain.model.Movie
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for MovieRepository.
 * Tests the repository's ability to combine remote data with local favorites.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MovieRepositoryTest {

    private lateinit var remoteDataSource: MovieRemoteDataSource
    private lateinit var favoritesDataSource: FavoritesLocalDataSource
    private lateinit var repository: MovieRepository

    private val testMovies = listOf(
        Movie(
            id = "1",
            title = "The Matrix",
            posterUrl = "https://example.com/matrix.jpg",
            backdropUrl = "https://example.com/matrix_backdrop.jpg",
            rating = 8.7,
            releaseDate = "1999-03-31",
            duration = "2h 16m",
            synopsis = "A computer hacker learns about the true nature of reality.",
            director = "The Wachowskis",
            cast = listOf("Keanu Reeves", "Laurence Fishburne"),
            genres = listOf("Sci-Fi", "Action"),
            isFavorite = false
        ),
        Movie(
            id = "2",
            title = "Inception",
            posterUrl = "https://example.com/inception.jpg",
            backdropUrl = "https://example.com/inception_backdrop.jpg",
            rating = 8.8,
            releaseDate = "2010-07-16",
            duration = "2h 28m",
            synopsis = "A thief who steals corporate secrets through dream-sharing.",
            director = "Christopher Nolan",
            cast = listOf("Leonardo DiCaprio", "Ellen Page"),
            genres = listOf("Sci-Fi", "Thriller"),
            isFavorite = false
        )
    )

    @Before
    fun setup() {
        remoteDataSource = mockk(relaxed = true)
        favoritesDataSource = mockk(relaxed = true)
        
        coEvery { remoteDataSource.getMovies() } returns testMovies
        every { favoritesDataSource.getAllFavoriteIds() } returns flowOf(emptySet())
        
        repository = MovieRepository(remoteDataSource, favoritesDataSource)
    }

    @Test
    fun `getMovies returns movies with isFavorite based on local favorites`() = runTest {
        // Setup: Movie "1" is favorite
        every { favoritesDataSource.getAllFavoriteIds() } returns flowOf(setOf("1"))
        repository = MovieRepository(remoteDataSource, favoritesDataSource)
        
        // Fetch movies first
        repository.fetchMovies()
        
        repository.getMovies().test {
            val movies = awaitItem()
            
            val matrix = movies.find { it.id == "1" }
            val inception = movies.find { it.id == "2" }
            
            assertTrue(matrix?.isFavorite == true, "Matrix should be favorite")
            assertFalse(inception?.isFavorite == true, "Inception should not be favorite")
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getFavorites returns only favorite movies`() = runTest {
        every { favoritesDataSource.getAllFavoriteIds() } returns flowOf(setOf("1"))
        repository = MovieRepository(remoteDataSource, favoritesDataSource)
        
        repository.fetchMovies()
        
        repository.getFavorites().test {
            val favorites = awaitItem()
            
            assertEquals(1, favorites.size)
            assertEquals("1", favorites.first().id)
            assertTrue(favorites.first().isFavorite)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getFavorites returns empty when no favorites`() = runTest {
        every { favoritesDataSource.getAllFavoriteIds() } returns flowOf(emptySet())
        repository = MovieRepository(remoteDataSource, favoritesDataSource)
        
        repository.fetchMovies()
        
        repository.getFavorites().test {
            val favorites = awaitItem()
            assertTrue(favorites.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleFavorite calls favoritesDataSource`() = runTest {
        repository.toggleFavorite("1")
        
        coVerify { favoritesDataSource.toggleFavorite("1") }
    }

    @Test
    fun `getMovie returns correct movie with favorite status`() = runTest {
        every { favoritesDataSource.getAllFavoriteIds() } returns flowOf(setOf("2"))
        repository = MovieRepository(remoteDataSource, favoritesDataSource)
        
        repository.fetchMovies()
        
        repository.getMovie("2").test {
            val movie = awaitItem()
            
            assertEquals("2", movie?.id)
            assertEquals("Inception", movie?.title)
            assertTrue(movie?.isFavorite == true)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getMovie returns null for non-existent id`() = runTest {
        repository.fetchMovies()
        
        repository.getMovie("999").test {
            val movie = awaitItem()
            assertEquals(null, movie)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `favorites update reactively when local data changes`() = runTest {
        val favoritesFlow = MutableStateFlow<Set<String>>(emptySet())
        every { favoritesDataSource.getAllFavoriteIds() } returns favoritesFlow
        repository = MovieRepository(remoteDataSource, favoritesDataSource)
        
        repository.fetchMovies()
        
        repository.getMovies().test {
            // Initially no favorites
            var movies = awaitItem()
            assertTrue(movies.none { it.isFavorite })
            
            // Add favorite
            favoritesFlow.value = setOf("1")
            movies = awaitItem()
            
            val matrix = movies.find { it.id == "1" }
            assertTrue(matrix?.isFavorite == true)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
