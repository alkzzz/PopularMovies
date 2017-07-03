package com.example.administrator.popularmovies.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.adapter.MovieAdapter;
import com.example.administrator.popularmovies.model.Movie;
import com.example.administrator.popularmovies.rest.MovieClient;
import com.example.administrator.popularmovies.rest.MovieService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, MovieAdapter.ItemClickListener {

    private List<Movie.ResultsBean> mMoviesList;
    private String userPref = "";
    private RecyclerView recyclerView;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv_movie_poster);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        makeRequest();
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
                mMoviesList = response.body().getResults();
                recyclerView.setAdapter(new MovieAdapter(MainActivity.this, mMoviesList, MainActivity.this));
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Connection Failed!!!", Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(this, DetailActivity.class);
        Movie.ResultsBean movie = mMoviesList.get(position);
        int id = movie.getId();
        intent.putExtra("movie_id", id);
        startActivity(intent);
    }
}
