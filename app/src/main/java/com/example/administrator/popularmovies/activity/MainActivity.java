package com.example.administrator.popularmovies.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Parcelable;
import android.os.PersistableBundle;
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
import com.example.administrator.popularmovies.model.MovieReview;
import com.example.administrator.popularmovies.model.MovieTrailer;
import com.example.administrator.popularmovies.sync.MovieSync;
import com.example.administrator.popularmovies.adapter.MoviePosterAdapter;
import com.example.administrator.popularmovies.data.MovieContract;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        MoviePosterAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private String userPref = "";
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private MoviePosterAdapter mMoviePosterAdapter;
    private GridLayoutManager mLayoutManager;
    private Parcelable state;

    private static final int ID_MOVIE_POPULAR_LOADER = 9;
    private static final int ID_MOVIE_TOP_RATED_LOADER = 19;
    private static final int ID_MOVIE_FAVORITE_LOADER = 29;

    private static final int INDEX_MOVIE_ID = 1;

    private static final String MOVIE_POSTER_STATE = "POSTER_POSITION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.rv_movie_poster);
        mLayoutManager = new  GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);

        mMoviePosterAdapter = new MoviePosterAdapter(this, this);
        recyclerView.setAdapter(mMoviePosterAdapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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
            getSupportLoaderManager().restartLoader(ID_MOVIE_POPULAR_LOADER, null, this);
        } else if (userPref.equals(getString(R.string.top_rated_value))) {
            getSupportLoaderManager().restartLoader(ID_MOVIE_TOP_RATED_LOADER, null, this);
        } else if (userPref.equals(getString(R.string.favorite_value))) {
            getSupportLoaderManager().restartLoader(ID_MOVIE_FAVORITE_LOADER, null, this);
        }
    }

    @Override
    public void onItemClick(int position) {
        mCursor.moveToPosition(position);
        int movie_id = mCursor.getInt(INDEX_MOVIE_ID);
        Intent intent = new Intent(this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("movie_id", movie_id);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        state = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(MOVIE_POSTER_STATE,state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable(MOVIE_POSTER_STATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (state != null) {
            mLayoutManager.onRestoreInstanceState(state);
        }
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
        if (data.getCount() != 0) showPoster();
        mCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviePosterAdapter.swapCursor(null);
    }
}
