package com.example.administrator.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.data.MovieContract;
import com.example.administrator.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.PosterHolder> {
    private static final String POSTER_URL = "http://image.tmdb.org/t/p/w185/";
    private final Context mContext;
    private Cursor mCursor;
    private final ItemClickListener mItemClickListener;

    public static final int INDEX_MOVIE_ID = 1;
    public static final int INDEX_MOVIE_NAME = 2;
    public static final int INDEX_MOVIE_POSTER = 3;
    public static final int INDEX_MOVIE_IS_FAVORITE = 4;

    class PosterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView poster;

        PosterHolder(View itemView) {
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

    public MoviePosterAdapter(Context c, ItemClickListener itemClickListener) {
        mContext = c;
        mItemClickListener = itemClickListener;
    }

    public PosterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_poster_row, parent, false);
        return new PosterHolder(view);
    }

    @Override
    public void onBindViewHolder(final PosterHolder holder, int position) {
        mCursor.moveToPosition(position);
        Picasso.with(mContext)
                .load(POSTER_URL + mCursor.getString(INDEX_MOVIE_POSTER))
                .error(R.drawable.no_image)
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

}
