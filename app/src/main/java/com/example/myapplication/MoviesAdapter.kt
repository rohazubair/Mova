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

class MoviesAdapter : ListAdapter<MovieDataModal, MoviesAdapter.MovieViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movies_rv, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.movie_imageview)
        private val rating: TextView = itemView.findViewById(R.id.rating_tv)

        fun bind(movie: MovieDataModal) {
            // Bind movie data to views
            rating.text = movie.vote_average
            // Load image using Glide
            val requestOptions = RequestOptions().transform(RoundedCorners(30))
            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                .apply(requestOptions)
                .into(poster)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MovieDataModal>() {
        override fun areItemsTheSame(oldItem: MovieDataModal, newItem: MovieDataModal) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MovieDataModal, newItem: MovieDataModal) = oldItem == newItem
    }
}


//class MovieAdapter(private val movies: List<MovieDataModal>, private val context: Context) :  RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
//
//    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        //private val title: TextView = itemView.findViewById(R.id.movieTitle)
//        //private val overview: TextView = itemView.findViewById(R.id.movieOverview)
//        private val poster: ImageView = itemView.findViewById(R.id.movie_imageview)
//        private val rating: TextView = itemView.findViewById(R.id.rating_tv)
//
//        fun bind(movie: MovieDataModal, context: Context) {
//            rating.text = movie.rating
//            // Load image using Glide
//            val requestOptions = RequestOptions().transform(RoundedCorners(30))
//            Glide.with(itemView.context)
//                .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
//                .apply(requestOptions)
//                .into(poster)
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.movies_rv, parent, false)
//        return MovieViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
//        holder.bind(movies[position], context)
//    }
//
//    override fun getItemCount(): Int = movies.size

//    class MyViewHolder(val binding: MoviesRvBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val binding = MoviesRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return MyViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val movie = movies[position]
//        val requestOptions = RequestOptions().transform(RoundedCorners(30))
//
//        holder.binding.ratingTv.text = movie.rating
//        Glide.with(holder.itemView.context)
//            .load(movie.imgUrl)
//            .apply(requestOptions)
//            .into(holder.binding.movieImageview)
//    }
//
//    /*override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val movie = mList[position]
//        holder.binding.ratingTv.text = movie.rating
//        Glide.with(holder.itemView.context)
//            .load("https://www.google.com/search?client=ubuntu-sn&hs=w2C&sca_esv=7b18b02be1ed2b18&channel=fs&q=doctor+strange+image&udm=2&fbs=AEQNm0Aa4sjWe7Rqy32pFwRj0UkWd8nbOJfsBGGB5IQQO6L3J_86uWOeqwdnV0yaSF-x2joZDvir2QxhZkTA8rK1etu4ohtqlTKXOQ56HmFa2r_epkbRAe7iB17TPYYRmar9NQbtqLCypDKnkds0QbbF2aLJ8b6KqDHGpfrsETgDV5kKa710n491edL88sEzMXSG1Wpm6jkA&sa=X&ved=2ahUKEwjmz5PMza6HAxWMo_0HHZBGANIQtKgLegQIEhAB&biw=1296&bih=656&dpr=1#vhid=vweQKoJEvogS4M&vssid=mosaic")
//            .into(holder.binding.movieImageview)
//    }*/
//
//    override fun getItemCount(): Int {
//        return mList.size
//    }

//}
