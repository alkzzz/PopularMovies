package com.example.administrator.popularmovies.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.sync.MovieSync;
import com.example.administrator.popularmovies.adapter.MoviePosterAdapter;
import com.example.administrator.popularmovies.data.MovieContract;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        MoviePosterAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private String userPref = "";
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private MoviePosterAdapter mMoviePosterAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private SharedPreferences sharedPreferences;

    private static final int ID_MOVIE_POPULAR_LOADER = 9;
    private static final int ID_MOVIE_TOP_RATED_LOADER = 19;
    private static final int ID_MOVIE_FAVORITE_LOADER = 29;

    private static final int INDEX_MOVIE_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.rv_movie_poster);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        mMoviePosterAdapter = new MoviePosterAdapter(this, this);
        recyclerView.setAdapter(mMoviePosterAdapter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        showLoading();
        MovieSync.fetchMovieAndInsert(this);
        userPref = sharedPreferences.getString(getString(R.string.sort_by_key), getString(R.string.popular_value));
        if (userPref.equals(getString(R.string.popular_value))) {
            getSupportLoaderManager().initLoader(ID_MOVIE_POPULAR_LOADER, null, this);
        } else if (userPref.equals(getString(R.string.top_rated_value))) {
            getSupportLoaderManager().initLoader(ID_MOVIE_TOP_RATED_LOADER, null, this);
        } else if (userPref.equals(getString(R.string.favorite_value))) {
            getSupportLoaderManager().initLoader(ID_MOVIE_FAVORITE_LOADER, null, this);
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        mCursor.close();
    }

    private void showPoster() {
        mProgressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
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
        if (userPref.equals(getString(R.string.popular_value))) {
            mCursor = getPopularMovies();
            mMoviePosterAdapter.swapCursor(mCursor);
        } else if (userPref.equals(getString(R.string.top_rated_value))) {
            mCursor = getTopRatedMovies();
            mMoviePosterAdapter.swapCursor(mCursor);
        } else if (userPref.equals(getString(R.string.favorite_value))) {
            mCursor = getFavoriteMovies();
            mMoviePosterAdapter.swapCursor(mCursor);
        }
    }

    private Cursor getPopularMovies() {
        return getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_CATEGORY + " = ?",
                new String[]{"popular"},
                null
        );
    }

    private Cursor getTopRatedMovies() {
        return getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_CATEGORY + " = ?",
                new String[]{"top_rated"},
                null
        );
    }

    private Cursor getFavoriteMovies() {
        return getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_IS_FAVORITE + " = 1",
                null,
                null
        );
    }

    @Override
    public void onItemClick(int position) {
        mCursor.moveToPosition(position);
        int movie_id = mCursor.getInt(INDEX_MOVIE_ID);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("movie_id", movie_id);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_MOVIE_POPULAR_LOADER:
                return new CursorLoader(
                        this,
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        MovieContract.MovieEntry.COLUMN_CATEGORY + " = ?",
                        new String[]{"popular"},
                        null
                );
            case ID_MOVIE_TOP_RATED_LOADER:
                return new CursorLoader(
                        this,
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        MovieContract.MovieEntry.COLUMN_CATEGORY + " = ?",
                        new String[]{"top_rated"},
                        null
                );
            case ID_MOVIE_FAVORITE_LOADER:
                return new CursorLoader(
                        this,
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        MovieContract.MovieEntry.COLUMN_IS_FAVORITE + " = 1",
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
        mCursor = data;
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        recyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showPoster();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviePosterAdapter.swapCursor(null);
    }
}
