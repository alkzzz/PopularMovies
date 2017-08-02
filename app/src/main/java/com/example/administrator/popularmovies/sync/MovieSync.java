package com.example.administrator.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.data.MovieContract;
import com.example.administrator.popularmovies.model.Movie;
import com.example.administrator.popularmovies.model.MovieDetail;
import com.example.administrator.popularmovies.model.MovieReview;
import com.example.administrator.popularmovies.model.MovieTrailer;
import com.example.administrator.popularmovies.rest.MovieClient;
import com.example.administrator.popularmovies.rest.MovieService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieSync {
    private static List<Movie.ResultsBean> mMoviesList;
    private static List<Object> items;
    private static List<MovieTrailer.ResultsBean> mTrailerList;

    public static void fetchMovieAndInsert(final Context context) {
        String apiKey = context.getString(R.string.api_key);
        MovieService apiService =
                MovieClient.getClient().create(MovieService.class);
        Call<Movie> popularMovies = apiService.getPopularMovies(apiKey);
        Call<Movie> topRatedMovies = apiService.getTopRatedMovies(apiKey);

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

    public static void fetchTrailerAndReview(final Context context, final int id) {
        String apiKey = context.getString(R.string.api_key);
        MovieService apiService =
                MovieClient.getClient().create(MovieService.class);
        Call<MovieTrailer> movieTrailerCall = apiService.getMovieTrailers(id, apiKey);
        Call<MovieReview> movieReviewCall = apiService.getMovieReviews(id, apiKey);

        movieTrailerCall.enqueue(new Callback<MovieTrailer>() {
            @Override
            public void onResponse(Call<MovieTrailer> call, Response<MovieTrailer> response) {
                List<MovieTrailer.ResultsBean> mTrailerList = response.body().getResults();
                bulkInsertTrailers(context, mTrailerList, id);
            }

            @Override
            public void onFailure(Call<MovieTrailer> call, Throwable t) {

            }
        });

        movieReviewCall.enqueue(new Callback<MovieReview>() {
            @Override
            public void onResponse(Call<MovieReview> call, Response<MovieReview> response) {
                List<MovieReview.ResultsBean> mReviewList = response.body().getResults();
                bulkInsertReviews(context, mReviewList, id);
            }

            @Override
            public void onFailure(Call<MovieReview> call, Throwable t) {

            }
        });
    }

    public static void fetchMovieDetailRuntime(final Context context, final int movie_id) {
        String apiKey = context.getString(R.string.api_key);
        final MovieService movieService =
                MovieClient.getClient().create(MovieService.class);

        Call<MovieDetail> call = movieService.getMovieDetails(movie_id, apiKey);
        call.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                int runtime = response.body().getRuntime();
                updateRuntimeValue(context, movie_id, runtime);
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {

            }
        });
    }

    private static void updateRuntimeValue(Context context, int movie_id, int runtime) {
        String id = String.valueOf(movie_id);
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(movie_id)).build();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, runtime);
        context.getContentResolver().update(
                uri,
                contentValues,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{id}
        );
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
            contentValues[i] = value;
            i++;
        }
        return contentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
    }

    private static long bulkInsertTrailers(Context context, List<MovieTrailer.ResultsBean> trailerList, int movieID) {
        Uri detailUri = MovieContract.MovieEntry.CONTENT_URI;
        detailUri = detailUri.buildUpon().appendPath(String.valueOf(movieID)).build();
        Uri trailerUri = detailUri.buildUpon().appendPath(MovieContract.PATH_TRAILER).build();
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues[] contentValues = new ContentValues[trailerList.size()];
        int i = 0;
        for (MovieTrailer.ResultsBean trailer : trailerList) {
            ContentValues value = new ContentValues();
            value.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieID);
            value.put(MovieContract.TrailerEntry.COLUMN_NAME, trailer.getName());
            value.put(MovieContract.TrailerEntry.COLUMN_KEY, trailer.getKey());
            value.put(MovieContract.TrailerEntry.COLUMN_TYPE, trailer.getType());
            contentValues[i] = value;
            i++;
        }
        return contentResolver.bulkInsert(trailerUri, contentValues);
    }

    private static long bulkInsertReviews(Context context, List<MovieReview.ResultsBean> reviewList, int movieID) {
        Uri detailUri = MovieContract.MovieEntry.CONTENT_URI;
        detailUri = detailUri.buildUpon().appendPath(String.valueOf(movieID)).build();
        Uri reviewUri = detailUri.buildUpon().appendPath(MovieContract.PATH_REVIEW).build();
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues[] contentValues = new ContentValues[reviewList.size()];
        int i = 0;
        for (MovieReview.ResultsBean review : reviewList) {
            ContentValues value = new ContentValues();
            value.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieID);
            value.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review.getId());
            value.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
            value.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
            contentValues[i] = value;
            i++;
        }
        return contentResolver.bulkInsert(reviewUri, contentValues);
    }
}
