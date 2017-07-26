package com.example.administrator.popularmovies.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
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
import com.example.administrator.popularmovies.model.MovieDetail;
import com.example.administrator.popularmovies.model.MovieReview;
import com.example.administrator.popularmovies.model.MovieTrailer;
import com.example.administrator.popularmovies.rest.MovieClient;
import com.example.administrator.popularmovies.rest.MovieService;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.synopsisdivider)
    View mDivider;
    @BindView(R.id.progressBar)
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.VISIBLE);
        mScrollView.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        movie_id = intent.getIntExtra("movie_id", 0);
        mRvMovieTrailers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRvMovieReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        makeMovieRequest();
        movieTrailerRequest();
        movieReviewRequest();
    }

    private void makeMovieRequest() {
        String apiKey = getApplicationContext().getString(R.string.api_key);
        final MovieService movieService =
                MovieClient.getClient().create(MovieService.class);

        Call<MovieDetail> call = movieService.getMovieDetails(movie_id, apiKey);
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
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mScrollView.setVisibility(View.VISIBLE);
                } else {
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
                Toast.makeText(DetailActivity.this, "Connection Failed!! " + t.getMessage(), Toast.LENGTH_LONG).show();
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
                        mTvNoTrailer.setText("No trailer found for this movie.");
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieTrailer> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Connection Failed!! " + t.getMessage(), Toast.LENGTH_LONG).show();
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
                        mTvNoReview.setText("No review found for this movie.");
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieReview> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Connection Failed!! " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        MovieTrailer.ResultsBean trailer = mTrailerList.get(position);
        String videoKey = trailer.getKey();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://youtube.com/watch?v=" + videoKey));
        startActivity(intent);
    }
}
