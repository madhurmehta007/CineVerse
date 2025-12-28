package com.android.cineverse.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.cineverse.data.paging.MoviePagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import org.android.cineverse.shared.data.repository.MovieRepository
import org.android.cineverse.shared.domain.model.Movie
import org.android.cineverse.shared.presentation.MoviesViewModel

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AndroidMoviesViewModel(private val repository: MovieRepository) : ViewModel() {
    private val sharedViewModel = MoviesViewModel(repository, viewModelScope)
    
    // Local search query with debounce
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    // Debounced movies filtering (for non-paged usage)
    val movies: StateFlow<List<Movie>> = combine(
        sharedViewModel.movies,
        _searchQuery.debounce(300) // 300ms debounce as per assignment
    ) { allMovies, query ->
        if (query.isBlank()) {
            allMovies
        } else {
            allMovies.filter { it.title.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Paged movies with search support
    val moviesPaged: Flow<PagingData<Movie>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false,
                    prefetchDistance = 3
                )
            ) {
                MoviePagingSource(repository, query)
            }.flow
        }
        .cachedIn(viewModelScope)

    val favorites = sharedViewModel.favorites

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
    
    fun toggleFavorite(movieId: String) = sharedViewModel.toggleFavorite(movieId)
}
