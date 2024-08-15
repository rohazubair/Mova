package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: MovieViewModel by activityViewModels()

    private lateinit var topRatedMoviesAdapter: MoviesAdapter
    private lateinit var newReleasedMoviesAdapter: MoviesAdapter

    private lateinit var headerImage: ImageView
    private lateinit var headerTitle: TextView
    private lateinit var textviewSeeAll1 : TextView
    private lateinit var textviewSeeAll2 : TextView

    private lateinit var database : MovieDatabase
    private lateinit var apiService : TmdbApiService
    private lateinit var movieDao : MovieDao
    private lateinit var repository : MovieRepository

    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0

    private val movieAdapterListener=object: MoviesAdapter.Listener{
        override fun addRemoveMovie(movie: MovieDataModal) {
            viewModel.insertMovie(movie)
        }

//        override fun deleteMovie(movieId: Int) {
//            viewModel.deleteMovie(movieId)
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topRatedMoviesAdapter = MoviesAdapter(listener = movieAdapterListener)
        newReleasedMoviesAdapter = MoviesAdapter(listener = movieAdapterListener)

        view.findViewById<RecyclerView>(R.id.top_10_movies).adapter = topRatedMoviesAdapter
        view.findViewById<RecyclerView>(R.id.new_releases).adapter = newReleasedMoviesAdapter

        headerImage = view.findViewById(R.id.header_image)
        headerTitle = view.findViewById(R.id.home_header_title_tv)

        database = MovieDatabase.getDatabase(requireContext())
        apiService = RetrofitInstance.api
        movieDao = database.movieDao()
        repository = MovieRepository.getInstance(apiService, movieDao)
        viewModel.setRepository(repository)
        viewModel.loadMovies()

        viewModel.fetchTopRatedMovies()
        viewModel.fetchNewReleasedMovies()
        viewModel.fetchPopularMovies()
        viewModel.fetchHeaderMovies()

        setupObservers()
        startHeaderUpdate()

        textviewSeeAll1 = view.findViewById(R.id.see_all_tv1)
        textviewSeeAll1.setOnClickListener { parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, Top10Fragment())
            .addToBackStack(null)
            .commit() }

        textviewSeeAll2 = view.findViewById(R.id.see_all_tv2)
        textviewSeeAll2.setOnClickListener { parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, NewReleaseFragment())
            .addToBackStack(null)
            .commit() }

    }

    private fun setupObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.headerMovies.collect { movies ->
                        if (movies.isNotEmpty()) {
                            updateHeader()
                        }
                    }
                }

                launch {
                    viewModel.topRatedMovies.collect { movies ->
                        topRatedMoviesAdapter.submitList(movies.take(10))
                    }
                }

                launch {
                    viewModel.newReleasedMovies.collect { movies ->
                        newReleasedMoviesAdapter.submitList(movies.take(10))
                    }
                }
            }
        }
    }

    private fun startHeaderUpdate() {
        handler.post(object : Runnable {
            override fun run() {
                updateHeader()
                handler.postDelayed(this, 6000) 
            }
        })
    }

    private fun updateHeader() {
        val movies = viewModel.headerMovies.value

        if (movies.isEmpty() ) {
            // Handling the case where movies or genres are empty
            Log.e("HomeFragment", "movies list is null")
            return
        }

        if (currentIndex >= movies.size) {
            // Handling the case where currentIndex is out of bounds
            currentIndex = 0
        }

        val movie = movies[currentIndex]

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
            .into(headerImage)
        headerTitle.text = movie.title

        // Increment the index or reset if it reaches the end of the list
        currentIndex = (currentIndex + 1) % movies.size
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null) // Stop the handler when the view is destroyed
    }

}