package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NewReleaseFragment : Fragment(R.layout.fragment_new_release) {

    private val viewModel: MovieViewModel by activityViewModels()

    private lateinit var newReleasesMoviesAdapter: MoviesAdapter

    private val newReleasesRecyclerView = view?.findViewById<RecyclerView>(R.id.rv_newReleases_movies)

    private lateinit var database : MovieDatabase
    private lateinit var apiService : TmdbApiService
    private lateinit var movieDao : MovieDao
    private lateinit var repository : MovieRepository

    private val movieAdapterListener = object: MoviesAdapter.Listener{
        override fun addRemoveMovie(movie: MovieDataModal) {
            viewModel.insertMovie(movie)
        }

//        override fun deleteMovie(movieId: Int) {
//            viewModel.deleteMovie(movieId)
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newReleasesMoviesAdapter = MoviesAdapter(listener = movieAdapterListener)
        newReleasesRecyclerView?.adapter = newReleasesMoviesAdapter

        database = MovieDatabase.getDatabase(requireContext())
        apiService = RetrofitInstance.api
        movieDao = database.movieDao()
        repository = MovieRepository.getInstance(apiService, movieDao)
        viewModel.setRepository(repository)
        viewModel.loadMovies()

        viewModel.fetchNewReleasedMovies()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newReleasedMovies.collect { movies ->
                    newReleasesMoviesAdapter.submitList(movies)
                }
            }
        }

        newReleasesRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMoreNewReleasedMovies()
                }
            }
        })

        val backArrow : ImageView = view.findViewById(R.id.newReleases_back)
        backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

}