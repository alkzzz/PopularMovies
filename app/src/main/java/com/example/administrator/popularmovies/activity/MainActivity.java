package com.example.administrator.popularmovies.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.adapter.MoviePosterAdapter;
import com.example.administrator.popularmovies.data.MovieContract;
import com.example.administrator.popularmovies.model.Movie;
import com.example.administrator.popularmovies.rest.MovieClient;
import com.example.administrator.popularmovies.rest.MovieService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        MoviePosterAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private List<Movie.ResultsBean> mMoviesList;
    private String userPref = "";
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private MoviePosterAdapter mMoviePosterAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    public static final String[] MAIN_FORECAST_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_NAME,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_IS_FAVORITE
    };

    public static final int INDEX_MOVIE_ID = 1;
    public static final int INDEX_MOVIE_NAME = 2;
    public static final int INDEX_MOVIE_POSTER = 3;
    public static final int INDEX_MOVIE_IS_FAVORITE = 4;

    private static final int ID_MOVIE_LOADER = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.rv_movie_poster);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        mMoviePosterAdapter = new MoviePosterAdapter(this, this);
        recyclerView.setAdapter(mMoviePosterAdapter);

        showLoading();

        makeRequest();

        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void showPoster() {
        mProgressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void makeRequest() {
        String apiKey = getApplicationContext().getString(R.string.api_key);
        MovieService apiService =
                MovieClient.getClient().create(MovieService.class);

        Call<Movie> popularMovies = apiService.getPopularMovies(apiKey, userPref);
        Call<Movie> highestRated = apiService.getHighestRatedMovies(apiKey, userPref);

        popularMovies.enqueue(new Callback<Movie>() {

            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if(response.isSuccessful()) {
                    mMoviesList = response.body().getResults();
                    bulkInsertMovies(mMoviesList, "popular");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {

            }
        });

        highestRated.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if(response.isSuccessful()) {
                    mMoviesList = response.body().getResults();
                    bulkInsertMovies(mMoviesList, "top_rated");
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

            }
        });
    }

    private long bulkInsertMovies(List<Movie.ResultsBean> movieList, String userPref) {
        ContentResolver contentResolver = getContentResolver();
        ContentValues[] contentValues = new ContentValues[movieList.size()];
        int i = 0;
        for (Movie.ResultsBean movie : movieList) {
            ContentValues value = new ContentValues();
            value.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            value.put(MovieContract.MovieEntry.COLUMN_NAME, movie.getTitle());
            value.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getPoster_path());
            value.put(MovieContract.MovieEntry.COLUMN_CATEGORY, userPref);
            value.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 0);
            contentValues[i] = value;
            i++;
        }
        return contentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.movie_setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.sort_by_key))) {
            userPref = sharedPreferences.getString(key, getString(R.string.popular_value));
        }
        makeRequest();
    }

    @Override
    public void onItemClick(int position) {
//        Movie.ResultsBean movie;
//        int id;
//        Intent intent = new Intent(this, DetailActivity.class);
//        if (mCursor != null && mCursor.moveToFirst()) {
//            mCursor.moveToPosition(position);
//            id = mCursor.getInt(INDEX_MOVIE_ID);
//        } else {
//            movie = mMoviesList.get(position);
//            id = movie.getId();
//        }
//        intent.putExtra("movie_id", id);
//        startActivity(intent);
    }

    private void getMoviesFromDb(String userPref) {
        ContentResolver contentResolver = getContentResolver();
//        if (userPref.equals(getString(R.string.favorite_value))) {
//            mCursor = contentResolver.query(
//                    MovieContract.MovieEntry.CONTENT_URI,
//                    null,
//                    MovieContract.MovieEntry.COLUMN_IS_FAVORITE + " = ?",
//                    new String[]{"1"},
//                    null
//            );
//        } else {
//            mCursor = contentResolver.query(
//                    MovieContract.MovieEntry.CONTENT_URI,
//                    null,
//                    MovieContract.MovieEntry.COLUMN_CATEGORY + " = ?",
//                    new String[]{userPref},
//                    null
//            );
////        }
//        MoviePosterAdapter moviePosterAdapter = new MoviePosterAdapter(MainActivity.this, MainActivity.this);
//        recyclerView.setAdapter(moviePosterAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_MOVIE_LOADER:
                return new CursorLoader(
                        this,
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
            default:
                throw new RuntimeException("Loader not implemented : "+id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviePosterAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        recyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showPoster();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviePosterAdapter.swapCursor(null);
    }
}
