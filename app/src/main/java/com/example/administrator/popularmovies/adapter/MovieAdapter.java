package com.example.administrator.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {
    private static final String POSTER_URL = "http://image.tmdb.org/t/p/w185/";
    private final Context mContext;
    private final List<Movie.ResultsBean> mMovieList;
    private final ItemClickListener mItemClickListener;

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView poster;

        MovieHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mItemClickListener.onItemClick(position);
        }
    }

    public MovieAdapter (Context c, List<Movie.ResultsBean> movieList, ItemClickListener itemClickListener) {
        mContext = c;
        mMovieList = movieList;
        mItemClickListener = itemClickListener;
    }

    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_row, parent, false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        Movie.ResultsBean movie = mMovieList.get(position);
        Picasso.with(mContext)
                .load(POSTER_URL+movie.getPoster_path())
                .error(R.drawable.no_image)
                .into(holder.poster);
    }


    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }
}
