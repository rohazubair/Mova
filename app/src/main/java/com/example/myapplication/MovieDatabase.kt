package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MovieDataModal::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        private var MovieDatabaseInstance: MovieDatabase? = null
        fun getDatabase(context: Context): MovieDatabase {
            if (MovieDatabaseInstance == null) {
                synchronized(this) {
                    MovieDatabaseInstance =
                        Room.databaseBuilder(context,
                            MovieDatabase::class.java,
                            "movie_database")
                            .build()
                }
            }
            return MovieDatabaseInstance!!
        }
    }
}


