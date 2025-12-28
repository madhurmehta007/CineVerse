package com.android.cineverse.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.first
import org.android.cineverse.shared.data.repository.MovieRepository
import org.android.cineverse.shared.domain.model.Movie

/**
 * PagingSource for loading movies with pagination.
 * 
 * Since our mock API returns all movies at once, we simulate pagination
 * by chunking the results. In a real API, you would pass page/offset
 * parameters to the server.
 */
class MoviePagingSource(
    private val repository: MovieRepository,
    private val searchQuery: String = ""
) : PagingSource<Int, Movie>() {

    companion object {
        private const val PAGE_SIZE = 10
        private const val INITIAL_PAGE = 0
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: INITIAL_PAGE
            
            // Get all movies from repository
            val allMovies = repository.getMovies().first()
            
            // Filter by search query if present
            val filteredMovies = if (searchQuery.isBlank()) {
                allMovies
            } else {
                allMovies.filter { it.title.contains(searchQuery, ignoreCase = true) }
            }
            
            // Calculate pagination
            val startIndex = page * PAGE_SIZE
            val endIndex = minOf(startIndex + PAGE_SIZE, filteredMovies.size)
            
            // Get page of movies
            val pageMovies = if (startIndex < filteredMovies.size) {
                filteredMovies.subList(startIndex, endIndex)
            } else {
                emptyList()
            }
            
            LoadResult.Page(
                data = pageMovies,
                prevKey = if (page == INITIAL_PAGE) null else page - 1,
                nextKey = if (endIndex >= filteredMovies.size) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
