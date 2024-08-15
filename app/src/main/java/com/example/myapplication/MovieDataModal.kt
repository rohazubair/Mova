package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieDataModal(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val vote_average: String
)

data class MovieResponse(
    val page: Int,
    val results: List<MovieDataModal>,
    val total_results: Int,
    val total_pages: Int
)