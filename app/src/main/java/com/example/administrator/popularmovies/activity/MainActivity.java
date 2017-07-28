package com.example.administrator.popularmovies.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.adapter.MoviePosterAdapter;
import com.example.administrator.popularmovies.data.MovieContract;
import com.example.administrator.popularmovies.data.MovieDbHelper;
import com.example.administrator.popularmovies.model.Movie;
import com.example.administrator.popularmovies.rest.MovieClient;
import com.example.administrator.popularmovies.rest.MovieService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, MoviePosterAdapter.ItemClickListener {

    private List<Movie.ResultsBean> mMoviesList;
    private String userPref = "";
    private RecyclerView recyclerView;
    private MoviePosterAdapter moviePosterAdapter;
    private Cursor mCursor;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv_movie_poster);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        mCursor = getMoviesFromDb();
        if (mCursor != null && mCursor.moveToFirst()) {
            moviePosterAdapter = new MoviePosterAdapter(MainActivity.this, mCursor, MainActivity.this);
            recyclerView.setAdapter(moviePosterAdapter);
        } else {
            makeRequest();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void makeRequest() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String apiKey = getApplicationContext().getString(R.string.api_key);
        userPref = sharedPreferences.getString(getString(R.string.sort_by_key), getString(R.string.popular_value));
        MovieService apiService =
                MovieClient.getClient().create(MovieService.class);

        Call<Movie> call = apiService.getMoviesList(apiKey, userPref);
        call.enqueue(new Callback<Movie>() {

            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if(response.isSuccessful()) {
                    mMoviesList = response.body().getResults();
                    moviePosterAdapter = new MoviePosterAdapter(MainActivity.this, mMoviesList, MainActivity.this);
                    recyclerView.setAdapter(moviePosterAdapter);
                    bulkInsertMovies(mMoviesList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Connection Failed!!! "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

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
        Movie.ResultsBean movie;
        int id;
        Intent intent = new Intent(this, DetailActivity.class);
        if (mCursor != null && mCursor.moveToFirst()) {
            mCursor.moveToPosition(position);
            id = mCursor.getInt(INDEX_MOVIE_ID);
        } else {
            movie = mMoviesList.get(position);
            id = movie.getId();
        }
        intent.putExtra("movie_id", id);
        startActivity(intent);
    }

    private long bulkInsertMovies(List<Movie.ResultsBean> movieList) {
        ContentResolver contentResolver = getContentResolver();
        ContentValues[] contentValues = new ContentValues[movieList.size()];
        int i = 0;
        for (Movie.ResultsBean movie : movieList) {
            ContentValues value = new ContentValues();
            value.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            value.put(MovieContract.MovieEntry.COLUMN_NAME, movie.getTitle());
            value.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getPoster_path());
            value.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 0);
            contentValues[i] = value;
            i++;
        }
        return contentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
    }

    private Cursor getMoviesFromDb() {
        ContentResolver contentResolver = getContentResolver();
        mCursor = contentResolver.query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
                );
        return mCursor;
    }

}
