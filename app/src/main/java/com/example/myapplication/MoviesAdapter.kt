package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class MoviesAdapter(private val layout: Int = R.layout.movies_rv, private val listener: Listener) : ListAdapter<MovieDataModal, MoviesAdapter.MovieViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MovieViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MovieViewHolder(itemView: View,  private val listener: Listener) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.movie_imageview)
        private val rating: TextView = itemView.findViewById(R.id.rating_tv)
        private val addRemoveBtnImageview: ImageView = itemView.findViewById(R.id.movie_addRemove)


        fun bind(movie: MovieDataModal) {
            // Bind movie data to views
            rating.text = movie.vote_average.removeRange(3,movie.vote_average.length)
            // Load image using Glide
            val requestOptions = RequestOptions().transform(RoundedCorners(30))
            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                .apply(requestOptions)
                .into(poster)

            addRemoveBtnImageview.setOnClickListener {
                listener.addRemoveMovie(movie)
            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MovieDataModal>() {
        override fun areItemsTheSame(oldItem: MovieDataModal, newItem: MovieDataModal) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MovieDataModal, newItem: MovieDataModal) = oldItem == newItem
    }

    interface Listener{
        fun addRemoveMovie(movie: MovieDataModal)
//        fun deleteMovie(movieId: Int)
    }

}


