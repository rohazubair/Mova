package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch


class Top10Fragment : Fragment(R.layout.fragment_top10) {

    private val viewModel: MovieViewModel by activityViewModels()

    private lateinit var top10MoviesAdapter: MoviesAdapter
    private lateinit var backArrow : ImageView
    private lateinit var topRatedRecyclerView : RecyclerView

    private lateinit var database : MovieDatabase
    private lateinit var apiService : TmdbApiService
    private lateinit var movieDao : MovieDao
    private lateinit var repository : MovieRepository

    private val movieAdapterListener=object: MoviesAdapter.Listener{
        override fun addRemoveMovie(movie: MovieDataModal) {
            viewModel.insertMovie(movie)
            Toast.makeText(requireContext(), "Movie added to MyList", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        top10MoviesAdapter = MoviesAdapter(listener = movieAdapterListener)

        backArrow = view.findViewById(R.id.top10_back)
        topRatedRecyclerView = view.findViewById(R.id.rv_top10_movies)
        topRatedRecyclerView.adapter = top10MoviesAdapter

        database = MovieDatabase.getDatabase(requireContext())
        apiService = RetrofitInstance.api
        movieDao = database.movieDao()
        repository = MovieRepository.getInstance(apiService, movieDao)
        viewModel.setRepository(repository)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.topRatedMovies.collect { movies ->
                    top10MoviesAdapter.submitList(movies)
                }
            }
        }

        topRatedRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMoreTopRatedMovies()
                }
            }
        })

        val backArrow = view.findViewById<ImageView>(R.id.top10_back)
        backArrow.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .hide(this@Top10Fragment)
                .show(parentFragmentManager.findFragmentByTag("NavigationFragment")!!)
                .show(parentFragmentManager.findFragmentByTag("HomeFragment")!!)
                .commit()
        }

    }

}