package com.android.cineverse.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.android.cineverse.shared.data.repository.MovieRepository
import org.android.cineverse.shared.presentation.MoviesViewModel

class AndroidMoviesViewModel(repository: MovieRepository) : ViewModel() {
    private val sharedViewModel = MoviesViewModel(repository, viewModelScope)

    val movies = sharedViewModel.movies
    val favorites = sharedViewModel.favorites
    val searchQuery = sharedViewModel.searchQuery

    fun onSearchQueryChanged(query: String) = sharedViewModel.onSearchQueryChanged(query)
    fun toggleFavorite(movieId: String) = sharedViewModel.toggleFavorite(movieId)
}
