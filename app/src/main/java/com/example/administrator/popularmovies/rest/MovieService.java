package com.example.administrator.popularmovies.rest;

import com.example.administrator.popularmovies.model.Movie;
import com.example.administrator.popularmovies.model.MovieDetail;
import com.example.administrator.popularmovies.model.MovieReview;
import com.example.administrator.popularmovies.model.MovieTrailer;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieService {
    @GET("movie/popular")
    Call<Movie> getPopularMovies(@Query("api_key") String apiKey);
    @GET("movie/top_rated")
    Call<Movie> getTopRatedMovies(@Query("api_key") String apiKey);
    @GET("movie/{id}")
    Call<MovieDetail> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);
    @GET("movie/{id}/videos")
    Call<MovieTrailer> getMovieTrailers(@Path("id") int id, @Query("api_key") String apiKey);
    @GET("movie/{id}/reviews")
    Call<MovieReview> getMovieReviews(@Path("id") int id, @Query("api_key") String apiKey);
}