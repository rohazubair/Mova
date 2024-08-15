package com.example.myapplication

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepository private constructor(private val apiService: TmdbApiService, private val movieDao: MovieDao) {

    suspend fun getTopRatedMovies(apiKey: String, page: Int): Flow<List<MovieDataModal>> = flow {
        val response = apiService.getTopRatedMovies(apiKey, page)
        emit(response.results)
    }

    suspend fun getNewReleasedMovies(apiKey: String, page: Int): Flow<List<MovieDataModal>> = flow {
        val response = apiService.getNewReleasedMovies(apiKey, page)
        emit(response.results)
    }

    suspend fun getPopularMovies(apiKey: String, page: Int): Flow<List<MovieDataModal>> = flow {
        val response = apiService.getPopularMovies(apiKey, page)
        emit(response.results)
    }

    suspend fun searchMovies(apiKey: String, query: String, page: Int): Flow<List<MovieDataModal>> = flow {
        val response = apiService.searchMovies(apiKey, query, page)
        emit(response.results)
    }

    suspend fun getMoviesByFilters(apiKey: String, genres: String?, region: String?, year: String?, sortBy: String?, page: Int): Flow<List<MovieDataModal>> = flow {
        val response = apiService.getMoviesByFilters(apiKey, genres, region, year, sortBy, page)
        emit(response.results)
    }

    suspend fun insertMovie(movie: MovieDataModal) {
        movieDao.insertMovie(movie)
    }

    fun getAllMovies(): Flow<List<MovieDataModal>> {
        return movieDao.getAllMovies()
    }

    suspend fun deleteMovie(movieId: Int) {
        movieDao.deleteMovie(movieId)
    }

    companion object {
        @Volatile private var instance: MovieRepository? = null

        fun getInstance(apiService: TmdbApiService, movieDao: MovieDao) =
            instance ?: synchronized(this) {
                instance ?: MovieRepository(apiService, movieDao).also { instance = it }
            }
    }
}