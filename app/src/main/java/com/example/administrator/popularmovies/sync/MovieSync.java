package com.example.administrator.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.data.MovieContract;
import com.example.administrator.popularmovies.model.Movie;
import com.example.administrator.popularmovies.rest.MovieClient;
import com.example.administrator.popularmovies.rest.MovieService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieSync {
    private static List<Movie.ResultsBean> mMoviesList;

    public static void fetchMovieAndInsert(final Context context) {
        String apiKey = context.getString(R.string.api_key);
        MovieService apiService =
                MovieClient.getClient().create(MovieService.class);
        Call<Movie> popularMovies = apiService.getPopularMovies(apiKey, context.getString(R.string.popular_value));
        Call<Movie> topRatedMovies = apiService.getTopRatedMovies(apiKey, context.getString(R.string.top_rated_value));

        popularMovies.enqueue(new Callback<Movie>() {

            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if(response.isSuccessful()) {
                    mMoviesList = response.body().getResults();
                    bulkInsertMovies(context, mMoviesList, "popular");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {

            }
        });

        topRatedMovies.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if(response.isSuccessful()) {
                    List<Movie.ResultsBean> mMoviesList = response.body().getResults();
                    bulkInsertMovies(context, mMoviesList, "top_rated");
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

            }
        });
    }

    private static long bulkInsertMovies(Context context, List<Movie.ResultsBean> movieList, String userPref) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues[] contentValues = new ContentValues[movieList.size()];
        int i = 0;
        for (Movie.ResultsBean movie : movieList) {
            ContentValues value = new ContentValues();
            value.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            value.put(MovieContract.MovieEntry.COLUMN_NAME, movie.getTitle());
            value.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getPoster_path());
            value.put(MovieContract.MovieEntry.COLUMN_CATEGORY, userPref);
            value.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movie.getOverview());
            value.put(MovieContract.MovieEntry.COLUMN_USER_RATING, movie.getVote_average());
            value.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());
            value.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 0);
            contentValues[i] = value;
            i++;
        }
        return contentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
    }
}
