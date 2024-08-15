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


class MyListFragment : Fragment(R.layout.fragment_mylist) {

    private val viewModel: MovieViewModel by activityViewModels()

    private lateinit var myListMoviesAdapter: MoviesAdapter

    private lateinit var myListMoviesRecyclerView: RecyclerView
    private lateinit var imageviewEmptyList: ImageView

    private lateinit var database : MovieDatabase
    private lateinit var apiService : TmdbApiService
    private lateinit var movieDao : MovieDao
    private lateinit var repository : MovieRepository

    private val movieAdapterListener=object: MoviesAdapter.Listener{
        override fun addRemoveMovie(movie: MovieDataModal) {
            viewModel.deleteMovie(movie.id)
        }

//        override fun deleteMovie(movieId: Int) {
//            viewModel.deleteMovie(movieId)
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myListMoviesAdapter = MoviesAdapter(R.layout.movies_rv_mylist, movieAdapterListener)

        myListMoviesRecyclerView= view.findViewById(R.id.rv_mylist)
        imageviewEmptyList= view.findViewById(R.id.imageview_empty)

        myListMoviesRecyclerView.adapter = myListMoviesAdapter

        database = MovieDatabase.getDatabase(requireContext())
        apiService = RetrofitInstance.api
        movieDao = database.movieDao()
        repository = MovieRepository.getInstance(apiService, movieDao)
        viewModel.setRepository(repository)
        viewModel.loadMovies()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movies.collect { movies ->
                    myListMoviesAdapter.submitList(movies)
                    if (movies.isNotEmpty()) {
                        showMyList()
                    } else {
                        showEmptyListImage()
                    }
                }
            }
        }
    }

    private fun showEmptyListImage() {
        myListMoviesRecyclerView.visibility = View.GONE
        imageviewEmptyList.visibility = View.VISIBLE
    }

    private fun showMyList() {
        myListMoviesRecyclerView.visibility = View.VISIBLE
        imageviewEmptyList.visibility = View.GONE
    }
}