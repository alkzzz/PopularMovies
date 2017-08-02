package com.example.administrator.popularmovies.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.adapter.MovieDetailAdapter;
import com.example.administrator.popularmovies.data.MovieContract;
import com.example.administrator.popularmovies.sync.MovieSync;


import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.pb_movieDetail)
    ProgressBar mPbMovieDetail;
    @BindView(R.id.rv_movie_detail)
    RecyclerView mRvMovieDetail;
    private int movie_id;
    private MovieDetailAdapter mMovieDetailAdapter;

    private static final int ID_MOVIE_DETAIL_LOADER = 9;
    private static final int ID_MOVIE_TRAILER_LOADER = 19;
    private static final int ID_MOVIE_REVIEW_LOADER = 29;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        showLoading();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.isEmpty()) {
            return;
        } else {
            movie_id = bundle.getInt("movie_id");
        }
        MovieSync.fetchMovieDetailRuntime(this, movie_id);
        MovieSync.fetchTrailerAndReview(this, movie_id);

        mRvMovieDetail.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mMovieDetailAdapter = new MovieDetailAdapter(this);
        mRvMovieDetail.setAdapter(mMovieDetailAdapter);
        getSupportLoaderManager().initLoader(ID_MOVIE_DETAIL_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_MOVIE_TRAILER_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_MOVIE_REVIEW_LOADER, null, this);
    }

    private void showLoading() {
        mPbMovieDetail.setVisibility(View.VISIBLE);
        mRvMovieDetail.setVisibility(View.INVISIBLE);
    }

    private void showMovie() {
        mPbMovieDetail.setVisibility(View.INVISIBLE);
        mRvMovieDetail.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri detailUri = MovieContract.MovieEntry.CONTENT_URI;
        detailUri = detailUri.buildUpon().appendPath(String.valueOf(movie_id)).build();
        Uri trailerUri = detailUri.buildUpon().appendPath(MovieContract.PATH_TRAILER).build();
        Uri reviewUri = detailUri.buildUpon().appendPath(MovieContract.PATH_REVIEW).build();
        switch (id) {
            case (ID_MOVIE_DETAIL_LOADER):
                return new CursorLoader(
                        this,
                        detailUri,
                        null,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(movie_id)},
                        null
                );
            case (ID_MOVIE_TRAILER_LOADER):
                return new CursorLoader(
                        this,
                        trailerUri,
                        null,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?" +
                                "AND " + MovieContract.TrailerEntry.COLUMN_TYPE + " = ?",
                        new String[]{String.valueOf(movie_id), "Trailer"},
                        null
                );
            case (ID_MOVIE_REVIEW_LOADER):
                return new CursorLoader(
                        this,
                        reviewUri,
                        null,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(movie_id)},
                        null
                );
            default:
                throw new RuntimeException("Loader not implemented : "+id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case ID_MOVIE_DETAIL_LOADER:
                mMovieDetailAdapter.swapMovieCursor(data);
                break;
            case ID_MOVIE_TRAILER_LOADER:
                mMovieDetailAdapter.swapTrailerCursor(data);
                break;
            case ID_MOVIE_REVIEW_LOADER:
                mMovieDetailAdapter.swapReviewCursor(data);
                break;
        }
        if (data.getCount() != 0) {
            showMovie();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieDetailAdapter.swapMovieCursor(null);
        mMovieDetailAdapter.swapTrailerCursor(null);
        mMovieDetailAdapter.swapReviewCursor(null);
    }

}
