package com.example.administrator.popularmovies.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.adapter.MovieReviewsAdapter;
import com.example.administrator.popularmovies.adapter.MovieTrailersAdapter;
import com.example.administrator.popularmovies.data.MovieContract;
import com.example.administrator.popularmovies.model.MovieDetail;
import com.example.administrator.popularmovies.model.MovieReview;
import com.example.administrator.popularmovies.model.MovieTrailer;
import com.example.administrator.popularmovies.rest.MovieClient;
import com.example.administrator.popularmovies.rest.MovieService;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements MovieTrailersAdapter.ItemClickListener {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv_poster_detail)
    ImageView mIvPosterDetail;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.tv_runtime)
    TextView mTvRuntime;
    @BindView(R.id.tv_vote_average)
    TextView mTvVoteAverage;
    @BindView(R.id.mark_favorite)
    TextView mMarkFavorite;
    @BindView(R.id.tv_synopsis)
    TextView mTvSynopsis;
    @BindView(R.id.pb_movieDetail)
    ProgressBar mProgressBar;
    @BindView(R.id.rv_movie_trailers)
    RecyclerView mRvMovieTrailers;
    @BindView(R.id.rv_movie_reviews)
    RecyclerView mRvMovieReviews;
    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;
    @BindView(R.id.tv_no_trailer)
    TextView mTvNoTrailer;
    @BindView(R.id.tv_no_review)
    TextView mTvNoReview;

    private int movie_id;
    private static final String POSTER_URL = "http://image.tmdb.org/t/p/w342";
    private List<MovieTrailer.ResultsBean> mTrailerList;
    private List<MovieReview.ResultsBean> mReviewList;
    private Cursor mCursor;

    private static final int INDEX_MOVIE_ID = 1;
    private static final int INDEX_MOVIE_NAME = 2;
    private static final int INDEX_MOVIE_POSTER = 3;
    private static final int INDEX_MOVIE_CATEGORY = 4;
    private static final int INDEX_MOVIE_SYNOPSIS = 5;
    private static final int INDEX_MOVIE_USER_RATING = 6;
    private static final int INDEX_MOVIE_RELEASE_DATE = 7;
    private static final int INDEX_MOVIE_IS_FAVORITE = 8;

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
        mRvMovieTrailers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRvMovieReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mCursor = fetchMovieDetailsFromDb(movie_id);
        if (haveInternetConnection()) {
            fillDetail(mCursor);
            movieTrailerRequest();
            movieReviewRequest();
        } else {
            fillDetail(mCursor);
        }
        showMovie();
    }

    public boolean haveInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void showLoading()
    {
        mProgressBar.setVisibility(View.VISIBLE);
        mScrollView.setVisibility(View.INVISIBLE);
    }

    private void showMovie()
    {
        mProgressBar.setVisibility(View.INVISIBLE);
        mScrollView.setVisibility(View.VISIBLE);
    }

    private Cursor fetchMovieDetailsFromDb(int movie_id) {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(movie_id)).build();
        ContentResolver contentResolver = getContentResolver();
        String movieID = String.valueOf(movie_id);
        mCursor = contentResolver.query(
                uri,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{movieID},
                null
        );
        return mCursor;
    }

    private void fillDetail(Cursor cursor) {
        if (cursor.moveToFirst()) {
            mTvTitle.setText(cursor.getString(INDEX_MOVIE_NAME));
            mTvDate.setText(cursor.getString(INDEX_MOVIE_RELEASE_DATE).substring(0, 4));
            mTvVoteAverage.setText(String.valueOf(cursor.getFloat(INDEX_MOVIE_USER_RATING)));
            mTvSynopsis.setText(cursor.getString(INDEX_MOVIE_SYNOPSIS));
            if(haveInternetConnection()) {
                Picasso.with(getApplicationContext())
                        .load(POSTER_URL + cursor.getString(INDEX_MOVIE_POSTER))
                        .error(R.drawable.no_image)
                        .into(mIvPosterDetail);
                movieRuntimeRequest();
                movieTrailerRequest();
                movieReviewRequest();
            } else {
                mIvPosterDetail.setImageResource(R.drawable.no_image);
                mTvRuntime.setText(getString(R.string.no_connection_runtime));
                mRvMovieTrailers.setVisibility(View.GONE);
                mTvNoTrailer.setVisibility(View.VISIBLE);
                mTvNoTrailer.setText(getString(R.string.no_connection_trailer));
                mRvMovieReviews.setVisibility(View.GONE);
                mTvNoReview.setVisibility(View.VISIBLE);
                mTvNoReview.setText(getString(R.string.no_connection_review));
            }
            if (cursor.getInt(INDEX_MOVIE_IS_FAVORITE) == 0) {
                mMarkFavorite.setText(getString(R.string.mark_favorite));
                mMarkFavorite.setBackgroundColor(Color.GREEN);
                mMarkFavorite.setTextColor(Color.BLACK);
            }
            else if (cursor.getInt(INDEX_MOVIE_IS_FAVORITE) == 1) {
                mMarkFavorite.setText(getString(R.string.favorited));
                mMarkFavorite.setBackgroundColor(Color.RED);
                mMarkFavorite.setTextColor(Color.WHITE);
            }
        }
    }

    private void movieRuntimeRequest() {
        String apiKey = getApplicationContext().getString(R.string.api_key);
        final MovieService movieService =
                MovieClient.getClient().create(MovieService.class);

        Call<MovieDetail> call = movieService.getMovieDetails(movie_id, apiKey);
        call.enqueue(new Callback<MovieDetail>() {

            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                if (response.isSuccessful()) {
                    mTvRuntime.setText(getString(R.string.runtime, response.body().getRuntime()));
                } else {
                    showLoading();
                    Toast.makeText(DetailActivity.this, "Movie Not Found..", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                mTvRuntime.setText(getString(R.string.no_connection_runtime));
            }
        });
    }

    private void movieTrailerRequest() {
        String apiKey = getApplicationContext().getString(R.string.api_key);
        final MovieService movieService = MovieClient.getClient().create(MovieService.class);

        Call<MovieTrailer> call = movieService.getMovieTrailers(movie_id, apiKey);
        call.enqueue(new Callback<MovieTrailer>() {
            @Override
            public void onResponse(Call<MovieTrailer> call, Response<MovieTrailer> response) {
                if (response.isSuccessful()) {
                    mTrailerList = response.body().getResults();
                    if (mTrailerList.size() > 0) {
                        mRvMovieTrailers.setAdapter(new MovieTrailersAdapter(DetailActivity.this, mTrailerList, DetailActivity.this));
                    } else {
                        mRvMovieTrailers.setVisibility(View.GONE);
                        mTvNoTrailer.setVisibility(View.VISIBLE);
                        mTvNoTrailer.setText(getString(R.string.not_found_trailer));
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieTrailer> call, Throwable t) {
                mRvMovieTrailers.setVisibility(View.GONE);
                mTvNoTrailer.setVisibility(View.VISIBLE);
                mTvNoTrailer.setText(getString(R.string.no_connection_trailer));
            }
        });
    }

    private void movieReviewRequest() {
        String apiKey = getApplicationContext().getString(R.string.api_key);
        final MovieService movieService = MovieClient.getClient().create(MovieService.class);


        Call<MovieReview> call = movieService.getMovieReviews(movie_id, apiKey);
        call.enqueue(new Callback<MovieReview>() {
            @Override
            public void onResponse(Call<MovieReview> call, Response<MovieReview> response) {
                if (response.isSuccessful()) {
                    mReviewList = response.body().getResults();
                    if (mReviewList.size() > 0) {
                        mRvMovieReviews.setAdapter(new MovieReviewsAdapter(DetailActivity.this, mReviewList));
                    } else {
                        mRvMovieReviews.setVisibility(View.GONE);
                        mTvNoReview.setVisibility(View.VISIBLE);
                        mTvNoReview.setText(getString(R.string.not_found_review));
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieReview> call, Throwable t) {
                mRvMovieReviews.setVisibility(View.GONE);
                mTvNoReview.setVisibility(View.VISIBLE);
                mTvNoReview.setText(getString(R.string.no_connection_review));
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        MovieTrailer.ResultsBean trailer = mTrailerList.get(position);
        String videoKey = trailer.getKey();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://youtube.com/watch?v=" + videoKey));
        String title = getString(R.string.chooser);
        Intent chooser = Intent.createChooser(intent, title);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    @OnClick(R.id.mark_favorite)
    public void markFavorite() {
        if (mCursor.moveToFirst() && mCursor.getInt(INDEX_MOVIE_IS_FAVORITE) == 0) {
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(String.valueOf(movie_id)).build();
            String id = String.valueOf(movie_id);
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 1);
            getContentResolver().update(
                    uri,
                    contentValues,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{id}
            );
            mMarkFavorite.setText(getString(R.string.favorited));
            mMarkFavorite.setBackgroundColor(Color.RED);
            mMarkFavorite.setTextColor(Color.WHITE);
        } else if (mCursor.moveToFirst() && mCursor.getInt(INDEX_MOVIE_IS_FAVORITE) == 1) {
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(String.valueOf(movie_id)).build();
            String id = String.valueOf(movie_id);
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 0);
            getContentResolver().update(
                    uri,
                    contentValues,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{id}
            );
            mMarkFavorite.setText(getString(R.string.mark_favorite));
            mMarkFavorite.setBackgroundColor(Color.GREEN);
            mMarkFavorite.setTextColor(Color.BLACK);
        }
    }

    @Override
    protected void onDestroy() {
        mCursor.close();
        super.onDestroy();
    }
}
