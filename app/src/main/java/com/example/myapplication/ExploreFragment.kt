package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExploreFragment : Fragment(R.layout.fragment_explore) {

    private val viewModel: MovieViewModel by activityViewModels()

    private lateinit var searchResultsAdapter: MoviesAdapter
    private lateinit var popularMoviesAdapter: MoviesAdapter
    private lateinit var filterResultsAdapter: MoviesAdapter

    private lateinit var popularMoviesRecyclerView: RecyclerView
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var filterResultsRecyclerView: RecyclerView
    private lateinit var imageview404: ImageView
    private lateinit var searchEditText: EditText

    private lateinit var database : MovieDatabase
    private lateinit var apiService : TmdbApiService
    private lateinit var movieDao : MovieDao
    private lateinit var repository : MovieRepository

    private lateinit var filterBottomSheetFragment: BottomSheetFragment

    private var searchJob: Job? = null

    private val movieAdapterListener=object: MoviesAdapter.Listener{
        override fun addRemoveMovie(movie: MovieDataModal) {
            viewModel.insertMovie(movie)
            Toast.makeText(requireContext(), "Movie added to MyList", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        popularMoviesAdapter = MoviesAdapter(listener = movieAdapterListener)
        searchResultsAdapter = MoviesAdapter(listener = movieAdapterListener)
        filterResultsAdapter = MoviesAdapter(listener = movieAdapterListener)

        popularMoviesRecyclerView = view.findViewById(R.id.rv_explore_movies)
        searchResultsRecyclerView = view.findViewById(R.id.rv_search_explore_movies)
        filterResultsRecyclerView = view.findViewById(R.id.rv_filter_explore_movies)
        imageview404 = view.findViewById(R.id.imageview_404)
        searchEditText = view.findViewById(R.id.explore_searchbar)

        popularMoviesRecyclerView.adapter = popularMoviesAdapter
        searchResultsRecyclerView.adapter = searchResultsAdapter
        filterResultsRecyclerView.adapter = filterResultsAdapter

        database = MovieDatabase.getDatabase(requireContext())
        apiService = RetrofitInstance.api
        movieDao = database.movieDao()
        repository = MovieRepository.getInstance(apiService, movieDao)
        viewModel.setRepository(repository)

        viewModel.fetchPopularMovies()

        setupObservers()

        popularMoviesRecyclerView.addOnScrollListener(createOnScrollListener { viewModel.loadMorePopularMovies() })
        searchResultsRecyclerView.addOnScrollListener(createOnScrollListener { viewModel.loadMoreSearchResult(searchEditText.text.toString()) })
        filterResultsRecyclerView.addOnScrollListener(createOnScrollListener { viewModel.loadMoreFilteredMovies() })

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let {
                    searchJob?.cancel()
                    searchJob = lifecycleScope.launch {
                        delay(1300)
                        if (it.isNotEmpty()) {
                            viewModel.resetSearchResults()
                            viewModel.searchMovies(it)
                        } else {
                            viewModel.updatePopularMovies()
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val filterBtn: ImageView = view.findViewById(R.id.explore_filter)
        filterBottomSheetFragment = BottomSheetFragment()
        filterBtn.setOnClickListener{
            filterBottomSheetFragment.show(parentFragmentManager, filterBottomSheetFragment.tag)

            filterBottomSheetFragment.setOnFilterAppliedListener {genre, region, year, sortBy ->
                viewModel.resetFilteredMovies()
                viewModel.fetchFilteredMovies(genre, region, year, sortBy)
            }

            filterBottomSheetFragment.setOnResetListener {
                viewModel.resetFilteredMovies()
                viewModel.updatePopularMovies()
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.popularMovies.collect { movies ->
                        popularMoviesAdapter.submitList(movies)
                        if (movies.isNotEmpty()) {
                            showPopularMovies()
                        } else {
                            hideAll()
                        }
                    }
                }
                launch {
                    viewModel.searchResults.collect { movies ->
                        if (movies.isEmpty() && searchEditText.text.isNotEmpty()) {
                            show404Error()
                        } else if (movies.isNotEmpty()) {
                            searchResultsAdapter.submitList(movies)
                            showSearchResults()
                        }
                    }
                }
                launch {
                    viewModel.filteredMovies.collect { movies ->
                        filterResultsAdapter.submitList(movies)
                        if (movies.isNotEmpty()) {
                            showFilterResults()
                        } else {
                            hideAll()
                        }
                    }
                }
            }
        }
//        Handler(Looper.getMainLooper()).postDelayed({
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container_view, LoginFragment())
//                .commit()
//        }, 2000)
    }

    private fun showPopularMovies() {
        popularMoviesRecyclerView.visibility = View.VISIBLE
        searchResultsRecyclerView.visibility = View.GONE
        filterResultsRecyclerView.visibility = View.GONE
        imageview404.visibility = View.GONE
    }

    private fun showSearchResults() {
        popularMoviesRecyclerView.visibility = View.GONE
        searchResultsRecyclerView.visibility = View.VISIBLE
        filterResultsRecyclerView.visibility = View.GONE
        imageview404.visibility = View.GONE
    }

    private fun showFilterResults() {
        popularMoviesRecyclerView.visibility = View.GONE
        searchResultsRecyclerView.visibility = View.GONE
        filterResultsRecyclerView.visibility = View.VISIBLE
        imageview404.visibility = View.GONE
    }

    private fun show404Error() {
        popularMoviesRecyclerView.visibility = View.GONE
        searchResultsRecyclerView.visibility = View.GONE
        filterResultsRecyclerView.visibility = View.GONE
        imageview404.visibility = View.VISIBLE
    }

    private fun hideAll() {
        popularMoviesRecyclerView.visibility = View.GONE
        searchResultsRecyclerView.visibility = View.GONE
        filterResultsRecyclerView.visibility = View.GONE
        imageview404.visibility = View.GONE
    }

    private fun createOnScrollListener(loadMoreAction: () -> Unit) = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as? GridLayoutManager
            layoutManager?.let {
                val totalItemCount = it.itemCount
                val lastVisibleItemPosition = it.findLastVisibleItemPosition()

                if (lastVisibleItemPosition >= totalItemCount / 2) {
                    loadMoreAction()
                }
            }
        }
    }
//        Handler(Looper.getMainLooper()).postDelayed({
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container_view, LoginFragment())
//                .commit()
//        }, 2000)

    override fun onResume() {
        super.onResume()

        if (searchEditText.text.isNotEmpty()) {
            showSearchResults()
        } else {
            showPopularMovies()
        }
    }



}
