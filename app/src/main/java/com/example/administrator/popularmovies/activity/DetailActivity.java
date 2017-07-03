package com.example.administrator.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.model.MovieDetail;
import com.example.administrator.popularmovies.rest.MovieClient;
import com.example.administrator.popularmovies.rest.MovieService;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

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
    @BindView(R.id.divider)
    View mDivider;
    @BindView(R.id.tv_trailers)
    TextView mTvTrailers;
    @BindView(R.id.imageButton)
    ImageButton mImageButton;
    @BindView(R.id.trailer1)
    TextView mTrailer1;
    @BindView(R.id.imageButton1)
    ImageButton mImageButton1;
    @BindView(R.id.trailer2)
    TextView mTrailer2;

    private int movie_id;
    private static final String POSTER_URL = "http://image.tmdb.org/t/p/w342";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        movie_id = intent.getIntExtra("movie_id", 0);
        makeRequest();
    }

    private void makeRequest() {
        String apiKey = getApplicationContext().getString(R.string.api_key);
        final MovieService movieService =
                MovieClient.getClient().create(MovieService.class);

        Call<MovieDetail> call = movieService.getMovieDetail(movie_id, apiKey);
        call.enqueue(new Callback<MovieDetail>() {

            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                if (response.isSuccessful()) {
                    Picasso.with(DetailActivity.this)
                            .load(POSTER_URL + response.body().getPoster_path())
                            .error(R.drawable.no_image)
                            .into(mIvPosterDetail);
                    mTvTitle.setText(response.body().getTitle());
                    mTvDate.setText(response.body().getRelease_date().substring(0, 4));
                    mTvRuntime.setText(getString(R.string.runtime, response.body().getRuntime()));
                    mTvVoteAverage.setText(getString(R.string.vote_average, response.body().getVote_average()));
                    mTvSynopsis.setText(response.body().getOverview());
                } else {
                    Toast.makeText(DetailActivity.this, "Movie Not Found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Connection Failed!!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
