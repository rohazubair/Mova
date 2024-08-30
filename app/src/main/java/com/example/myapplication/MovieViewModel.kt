package com.example.myapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MovieViewModel() : ViewModel() {
    private var repository: MovieRepository? = null

    private val _topRatedMovies = MutableStateFlow<List<MovieDataModal>>(emptyList())
    val topRatedMovies: StateFlow<List<MovieDataModal>> = _topRatedMovies

    private val _newReleasedMovies = MutableStateFlow<List<MovieDataModal>>(emptyList())
    val newReleasedMovies: StateFlow<List<MovieDataModal>> = _newReleasedMovies

    private val _popularMovies = MutableStateFlow<List<MovieDataModal>>(emptyList())
    val popularMovies: StateFlow<List<MovieDataModal>> = _popularMovies

    private val _searchResults = MutableStateFlow<List<MovieDataModal>>(emptyList())
    val searchResults: StateFlow<List<MovieDataModal>> = _searchResults

    private val _filteredMovies = MutableStateFlow<List<MovieDataModal>>(emptyList())
    val filteredMovies: StateFlow<List<MovieDataModal>> = _filteredMovies

    private val _headerMovies = MutableStateFlow<List<MovieDataModal>>(emptyList())
    val headerMovies: StateFlow<List<MovieDataModal>> = _headerMovies

    private val _genres = MutableStateFlow<Map<Int, String>>(emptyMap())
    val genres: StateFlow<Map<Int, String>> = _genres

    private val _movies = MutableStateFlow<List<MovieDataModal>>(emptyList())
    val movies: StateFlow<List<MovieDataModal>> = _movies

    private var topRatedPage = 1
    private var newReleasedPage = 1
    private var popularPage = 1
    private var searchPage = 1
    private var filterPage = 1
    private val headerPage = 1

    private var currentGenres: String? = null
    private var currentRegion: String? = null
    private var currentYear: String? = null
    private var currentSortBy: String? = null

    private val apiKey = "316373081224cd654e971158dc41dc51"

    fun setRepository(repository: MovieRepository) {
        this.repository = repository
    }

    fun fetchTopRatedMovies() {
        Log.d("TAG", "fetchTopRatedMovies() called")
        viewModelScope.launch {
            repository?.let {
                it.getTopRatedMovies(apiKey, topRatedPage).collect { movies ->
                    _topRatedMovies.value += movies
                }
            } ?: throw UninitializedPropertyAccessException("Repository is not initialized")
        }
    }


    fun fetchNewReleasedMovies() {
        Log.d("TAG", "fetchNewReleasedMovies() called")

        viewModelScope.launch {
            repository?.let {
                it.getNewReleasedMovies(apiKey, newReleasedPage).collect { movies ->
                    _newReleasedMovies.value += movies
                }
            } ?: throw UninitializedPropertyAccessException("Repository is not initialized")
        }
    }


    fun fetchPopularMovies() {
        Log.d("TAG", "fetchPopularMovies() called")

        viewModelScope.launch {
            repository?.let {
                it.getPopularMovies(apiKey, popularPage).collect { movies ->
                    _popularMovies.value += movies
                }
            } ?: throw UninitializedPropertyAccessException("Repository is not initialized")
        }
    }

    fun loadMoreTopRatedMovies() {
        topRatedPage++
        fetchTopRatedMovies()
    }

    fun loadMoreNewReleasedMovies() {
        newReleasedPage++
        fetchNewReleasedMovies()
    }

    fun loadMorePopularMovies() {
        popularPage++
        fetchPopularMovies()
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            repository?.let {
                it.searchMovies(apiKey, query, searchPage).collect { movies ->
                    if (movies.isEmpty() && searchPage == 1) {
                        _searchResults.value = emptyList()
                    } else {
                        _searchResults.value += movies
                    }
                }
            } ?: throw UninitializedPropertyAccessException("Repository is not initialized")
        }
    }

    fun resetSearchResults() {
        searchPage = 1
        _searchResults.value = emptyList()
    }

    fun loadMoreSearchResult(query: String) {
        searchPage++
        searchMovies(query)
    }

    fun fetchFilteredMovies(genres: String?, region: String?, year: String?, sortBy: String?) {
        currentGenres = genres
        currentRegion = region
        currentYear = year
        currentSortBy = sortBy

        viewModelScope.launch {
            repository?.let {
                it.getMoviesByFilters(apiKey, genres, region, year, sortBy, filterPage).collect { movies ->
                    if (movies.isEmpty() && filterPage == 1) {
                        _filteredMovies.value = emptyList()
                    } else {
                        _filteredMovies.value += movies
                    }
                }
            } ?: throw UninitializedPropertyAccessException("Repository is not initialized")
        }
    }

    fun loadMoreFilteredMovies() {
        filterPage++
        fetchFilteredMovies(currentGenres, currentRegion, currentYear, currentSortBy)
    }

    fun resetFilteredMovies() {
        filterPage = 1
        _filteredMovies.value = emptyList()
    }

    fun fetchHeaderMovies() {
        Log.d("TAG", "fetchHeaderMovies() called")
        viewModelScope.launch {
            repository?.let {
                it.getPopularMovies(apiKey, headerPage).collect { movies ->
                    _headerMovies.value += movies
                }
            } ?: throw UninitializedPropertyAccessException("Repository is not initialized")
        }
    }

    fun loadMovies() {
        viewModelScope.launch {
            repository?.getAllMovies()?.collectLatest { movies ->
                _movies.value = movies
            }
        }
    }


    fun insertMovie(movie: MovieDataModal) {
        viewModelScope.launch {
            try {
                repository?.insertMovie(movie)
            } catch (e: Exception) {
                // Handle exception
                Log.e("MovieViewModel", "Error inserting movie", e)
            }
        }
    }

    fun deleteMovie(movieId: Int) {
        viewModelScope.launch {
            repository?.deleteMovie(movieId)
        }
    }

    fun updatePopularMovies() {
        popularPage = 1
        _popularMovies.value = emptyList()
        fetchPopularMovies()
    }
}
