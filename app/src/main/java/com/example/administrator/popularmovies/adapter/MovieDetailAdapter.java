package com.example.administrator.popularmovies.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.data.MovieContract;
import com.example.administrator.popularmovies.model.MovieReview;
import com.example.administrator.popularmovies.model.MovieTrailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.administrator.popularmovies.model.MovieReview.*;

public class MovieDetailAdapter extends RecyclerView.Adapter<MovieDetailAdapter.CustomViewHolder> {
    private static final String POSTER_URL = "http://image.tmdb.org/t/p/w342/";
    private final Context mContext;
    private Cursor mMovieCursor;
    private Cursor mTrailerCursor;
    private Cursor mReviewCursor;
    private final ItemClickListener mItemClickListener;

    private static final int VIEW_DETAIL = 100;
    private static final int VIEW_TRAILER = 200;
    private static final int VIEW_REVIEW = 300;

    private static final int INDEX_MOVIE_ID = 1;
    private static final int INDEX_MOVIE_TITLE = 2;
    private static final int INDEX_MOVIE_POSTER = 3;
    private static final int INDEX_MOVIE_CATEGORY = 4;
    private static final int INDEX_MOVIE_SYNOPSIS = 5;
    private static final int INDEX_MOVIE_USER_RATING = 6;
    private static final int INDEX_MOVIE_RELEASE_DATE = 7;
    private static final int INDEX_MOVIE_RUNTIME = 8;
    private static final int INDEX_MOVIE_IS_FAVORITE = 9;

    private static final int INDEX_TRAILER_MOVIE_ID = 1;
    private static final int INDEX_TRAILER_NAME = 2;
    private static final int INDEX_TRAILER_KEY = 3;
    private static final int INDEX_TRAILER_TYPE = 4;

    public MovieDetailAdapter(Context context, ItemClickListener itemClickListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemViewType(int position) {
        if (mMovieCursor != null && mMovieCursor.moveToPosition(position)) {
            return VIEW_DETAIL;
        }
        else if (mTrailerCursor != null && mTrailerCursor.moveToPosition(position)) {
            return VIEW_TRAILER;
        }
        else if(mReviewCursor != null && mReviewCursor.moveToPosition(position)) {
            return VIEW_REVIEW;
        }
        return 100;
    }

    @Override
    public int getItemCount() {
        if (mMovieCursor == null) return 0;
        int trailerCount = 0;
        int reviewCount = 0;
        int movieCount = mMovieCursor.getCount();
        if (mTrailerCursor != null && mTrailerCursor.getCount() > 0) {
            trailerCount = mTrailerCursor.getCount();
        }
        if (mReviewCursor != null && mReviewCursor.getCount() > 0) {
            reviewCount = mReviewCursor.getCount();
        }
        return (movieCount + trailerCount + reviewCount);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 100:
                View viewDetail = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_detail_row, parent, false);
                return new CustomViewHolder(viewDetail);

            case 200:
                View viewTrailer = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_trailers_row, parent, false);
                return new CustomViewHolder(viewTrailer);

            case 300:
                View viewReview = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_reviews_row, parent, false);
                return new CustomViewHolder(viewReview);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_DETAIL:
                configureDetailViewHolder(holder, position);
            case VIEW_TRAILER:
                configureDetailViewHolder(holder, position);
            case VIEW_REVIEW:
                configureDetailViewHolder(holder, position);

        }
    }

    private void configureDetailViewHolder(CustomViewHolder customViewHolder, int position) {
        if (mMovieCursor.moveToPosition(position)) {
            customViewHolder.tv_movie_title.setText(mMovieCursor.getString(INDEX_MOVIE_TITLE));
            customViewHolder.tv_movie_date.setText(mMovieCursor.getString(INDEX_MOVIE_RELEASE_DATE).substring(0, 4));
            customViewHolder.tv_movie_runtime.setText(mContext.getString(R.string.runtime, mMovieCursor.getInt(INDEX_MOVIE_RUNTIME)));
            customViewHolder.tv_movie_vote.setText(mContext.getString(R.string.vote_average, mMovieCursor.getFloat(INDEX_MOVIE_USER_RATING)));
            customViewHolder.tv_movie_synopsis.setText(mMovieCursor.getString(INDEX_MOVIE_SYNOPSIS));
            Picasso.with(mContext)
                    .load(POSTER_URL + mMovieCursor.getString(INDEX_MOVIE_POSTER))
                    .error(R.drawable.no_image)
                    .into(customViewHolder.iv_movie_poster);

            if (mMovieCursor.getInt(INDEX_MOVIE_IS_FAVORITE) == 1) {
                customViewHolder.tv_mark_favorite.setText(mContext.getString(R.string.favorited));
                customViewHolder.tv_mark_favorite.setBackgroundColor(Color.RED);
                customViewHolder.tv_mark_favorite.setTextColor(Color.WHITE);
            } else if (mMovieCursor.getInt(INDEX_MOVIE_IS_FAVORITE) == 0){
                customViewHolder.tv_mark_favorite.setText(mContext.getString(R.string.mark_favorite));
                customViewHolder.tv_mark_favorite.setBackgroundColor(Color.GREEN);
                customViewHolder.tv_mark_favorite.setTextColor(Color.BLACK);
            }
        }
    }

    private void configureTrailerHolder(CustomViewHolder trailerHolder, int position) {
        if (mTrailerCursor.moveToFirst()) {
            trailerHolder.tv_trailerCount.setText("Trailer " + (position + 1));
            trailerHolder.tv_trailerName.setText(mTrailerCursor.getString(INDEX_TRAILER_NAME));
        }
    }

    private void configureReviewViewHolder(CustomViewHolder reviewHolder, int position) {
    }

    public void swapMovieCursor(Cursor newCursor) {
        mMovieCursor = newCursor;
        notifyDataSetChanged();
    }

    public void swapTrailerCursor(Cursor newCursor) {
        mTrailerCursor = newCursor;
        notifyDataSetChanged();
    }

    public void swapReviewCursor(Cursor newCursor) {
        mReviewCursor = newCursor;
        notifyDataSetChanged();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView tv_movie_title;
        final ImageView iv_movie_poster;
        final TextView tv_movie_date;
        final TextView tv_movie_runtime;
        final TextView tv_movie_vote;
        final TextView tv_mark_favorite;
        final TextView tv_movie_synopsis;

        final TextView tv_trailerCount;
        final TextView tv_trailerName;
        final ImageButton mImageButton;

        public CustomViewHolder(View itemView) {
            super(itemView);
            tv_movie_title = (TextView) itemView.findViewById(R.id.tv_movie_title);
            iv_movie_poster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            tv_movie_date = (TextView) itemView.findViewById(R.id.tv_movie_date);
            tv_movie_runtime = (TextView) itemView.findViewById(R.id.tv_movie_runtime);
            tv_movie_vote = (TextView) itemView.findViewById(R.id.tv_movie_vote);
            tv_movie_synopsis = (TextView) itemView.findViewById(R.id.tv_movie_synopsis);
            tv_mark_favorite = (TextView) itemView.findViewById(R.id.mark_favorite);

            tv_trailerCount = (TextView) itemView.findViewById(R.id.tv_trailer_count);
            tv_trailerName = (TextView) itemView.findViewById(R.id.tv_trailer_name);
            mImageButton = (ImageButton) itemView.findViewById(R.id.imgbtn_trailer);

//            tv_mark_favorite.setOnClickListener(this);
//            mImageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mMovieCursor.moveToFirst()) {
                markFavorite(mMovieCursor);
            }
        }
//
//    private class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        final TextView tv_trailerCount;
//        final TextView tv_trailerName;
//        final ImageButton mImageButton;
//
//        TrailerHolder(View itemView) {
//            super(itemView);
//            tv_trailerCount = (TextView) itemView.findViewById(R.id.tv_trailer_count);
//            tv_trailerName = (TextView) itemView.findViewById(R.id.tv_trailer_name);
//            mImageButton = (ImageButton) itemView.findViewById(R.id.imgbtn_trailer);
//
//            mImageButton.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            int position = getAdapterPosition();
//            mItemClickListener.onItemClick(position);
//        }
//    }
//
//    private class ReviewHolder extends RecyclerView.ViewHolder {
//
//        ReviewHolder(View itemView) {
//            super(itemView);
//
//        }
//    }

//    private class DetailHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        final TextView tv_movie_title;
//        final ImageView iv_movie_poster;
//        final TextView tv_movie_date;
//        final TextView tv_movie_runtime;
//        final TextView tv_movie_vote;
//        final TextView tv_mark_favorite;
//        final TextView tv_movie_synopsis;
//
//        DetailHolder(View itemView) {
//            super(itemView);
//            tv_movie_title = (TextView) itemView.findViewById(R.id.tv_movie_title);
//            iv_movie_poster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
//            tv_movie_date = (TextView) itemView.findViewById(R.id.tv_movie_date);
//            tv_movie_runtime = (TextView) itemView.findViewById(R.id.tv_movie_runtime);
//            tv_movie_vote = (TextView) itemView.findViewById(R.id.tv_movie_vote);
//            tv_movie_synopsis = (TextView) itemView.findViewById(R.id.tv_movie_synopsis);
//            tv_mark_favorite = (TextView) itemView.findViewById(R.id.mark_favorite);
//
//            tv_mark_favorite.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            if (mMovieCursor.moveToFirst()) {
//                markFavorite(mMovieCursor);
//            }
//        }
//    }

        private void markFavorite(Cursor cursor) {
            if (cursor.getInt(INDEX_MOVIE_IS_FAVORITE) == 0) {
                String movie_id = String.valueOf(mMovieCursor.getInt(INDEX_MOVIE_ID));
                Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(movie_id).build();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 1);
                mContext.getContentResolver().update(
                        uri,
                        contentValues,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{movie_id}
                );
            } else if (cursor.getInt(INDEX_MOVIE_IS_FAVORITE) == 1) {
                String movie_id = String.valueOf(cursor.getInt(INDEX_MOVIE_ID));
                Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(movie_id).build();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 0);
                mContext.getContentResolver().update(
                        uri,
                        contentValues,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{movie_id}
                );
            }
        }
    }
}
