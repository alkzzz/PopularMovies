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

public class DetailActivity extends AppCompatActivity implements
        MovieDetailAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.pb_movieDetail)
    ProgressBar mPbMovieDetail;
    @BindView(R.id.rv_movie_detail)
    RecyclerView mRvMovieDetail;
    private int movie_id;
    private MovieDetailAdapter mMovieDetailAdapter;

    private static final int ID_MOVIE_DETAIL_LOADER = 9;
    private static final int ID_MOVIE_TRAILER_LOADER = 19;

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

        mMovieDetailAdapter = new MovieDetailAdapter(this, this);
        mRvMovieDetail.setAdapter(mMovieDetailAdapter);
        getSupportLoaderManager().initLoader(ID_MOVIE_DETAIL_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_MOVIE_TRAILER_LOADER, null, this);
    }

    public boolean haveInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
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
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?"
                               + "AND "+ MovieContract.TrailerEntry.COLUMN_TYPE + " = ?",
                        new String[]{String.valueOf(movie_id), "Trailer"},
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

    @Override
    public void onItemClick(int position) {

    }

//    private void fillDetail(Cursor cursor) {
//        if (cursor.moveToFirst()) {
//            mTvTitle.setText(cursor.getString(INDEX_MOVIE_NAME));
//            mTvDate.setText(cursor.getString(INDEX_MOVIE_RELEASE_DATE).substring(0, 4));
//            mTvVoteAverage.setText(String.valueOf(cursor.getFloat(INDEX_MOVIE_USER_RATING)));
//            mTvSynopsis.setText(cursor.getString(INDEX_MOVIE_SYNOPSIS));
//            if (haveInternetConnection()) {
//                Picasso.with(getApplicationContext())
//                        .load(POSTER_URL + cursor.getString(INDEX_MOVIE_POSTER))
//                        .error(R.drawable.no_image)
//                        .into(mIvPosterDetail);
//                movieRuntimeRequest();
//                movieTrailerRequest();
//                movieReviewRequest();
//            } else {
//                mIvPosterDetail.setImageResource(R.drawable.no_image);
//                mTvRuntime.setText(getString(R.string.no_connection_runtime));
//                mRvMovieTrailers.setVisibility(View.GONE);
//                mTvNoTrailer.setVisibility(View.VISIBLE);
//                mTvNoTrailer.setText(getString(R.string.no_connection_trailer));
//                mRvMovieReviews.setVisibility(View.GONE);
//                mTvNoReview.setVisibility(View.VISIBLE);
//                mTvNoReview.setText(getString(R.string.no_connection_review));
//            }
//            if (cursor.getInt(INDEX_MOVIE_IS_FAVORITE) == 0) {
//                mMarkFavorite.setText(getString(R.string.mark_favorite));
//                mMarkFavorite.setBackgroundColor(Color.GREEN);
//                mMarkFavorite.setTextColor(Color.BLACK);
//            } else if (cursor.getInt(INDEX_MOVIE_IS_FAVORITE) == 1) {
//                mMarkFavorite.setText(getString(R.string.favorited));
//                mMarkFavorite.setBackgroundColor(Color.RED);
//                mMarkFavorite.setTextColor(Color.WHITE);
//            }
//        }
//    }

//    private void movieTrailerRequest() {
//        String apiKey = getApplicationContext().getString(R.string.api_key);
//        final MovieService movieService = MovieClient.getClient().create(MovieService.class);
//
//        Call<MovieTrailer> call = movieService.getMovieTrailers(movie_id, apiKey);
//        call.enqueue(new Callback<MovieTrailer>() {
//            @Override
//            public void onResponse(Call<MovieTrailer> call, Response<MovieTrailer> response) {
//                if (response.isSuccessful()) {
//                    mTrailerList = response.body().getResults();
//                    if (mTrailerList.size() > 0) {
//                        mRvMovieTrailers.setAdapter(new MovieTrailersAdapter(DetailActivity.this, mTrailerList, DetailActivity.this));
//                    } else {
//                        mRvMovieTrailers.setVisibility(View.GONE);
//                        mTvNoTrailer.setVisibility(View.VISIBLE);
//                        mTvNoTrailer.setText(getString(R.string.not_found_trailer));
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MovieTrailer> call, Throwable t) {
//                mRvMovieTrailers.setVisibility(View.GONE);
//                mTvNoTrailer.setVisibility(View.VISIBLE);
//                mTvNoTrailer.setText(getString(R.string.no_connection_trailer));
//            }
//        });
//    }

//    private void movieReviewRequest() {
//        String apiKey = getApplicationContext().getString(R.string.api_key);
//        final MovieService movieService = MovieClient.getClient().create(MovieService.class);
//
//
//        Call<MovieReview> call = movieService.getMovieReviews(movie_id, apiKey);
//        call.enqueue(new Callback<MovieReview>() {
//            @Override
//            public void onResponse(Call<MovieReview> call, Response<MovieReview> response) {
//                if (response.isSuccessful()) {
//                    mReviewList = response.body().getResults();
//                    if (mReviewList.size() > 0) {
//                        mRvMovieReviews.setAdapter(new MovieReviewsAdapter(DetailActivity.this, mReviewList));
//                    } else {
//                        mRvMovieReviews.setVisibility(View.GONE);
//                        mTvNoReview.setVisibility(View.VISIBLE);
//                        mTvNoReview.setText(getString(R.string.not_found_review));
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MovieReview> call, Throwable t) {
//                mRvMovieReviews.setVisibility(View.GONE);
//                mTvNoReview.setVisibility(View.VISIBLE);
//                mTvNoReview.setText(getString(R.string.no_connection_review));
//            }
//        });
//    }

//    @Override
//    public void onItemClick(int position) {
//        MovieTrailer.ResultsBean trailer = mTrailerList.get(position);
//        String videoKey = trailer.getKey();
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse("https://youtube.com/watch?v=" + videoKey));
//        String title = getString(R.string.chooser);
//        Intent chooser = Intent.createChooser(intent, title);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(chooser);
//        }
//    }
//

}
