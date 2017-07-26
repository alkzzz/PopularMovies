package com.example.administrator.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.model.MovieReview;

import java.util.List;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ReviewHolder> {
    private final Context mContext;
    private final List<MovieReview.ResultsBean> mReviewList;

    public MovieReviewsAdapter(Context context, List<MovieReview.ResultsBean> reviewList) {
        mContext = context;
        mReviewList = reviewList;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_reviews_row, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        MovieReview.ResultsBean review = mReviewList.get(position);
        holder.mTv_review_author.setText(review.getAuthor()+" :");
        holder.mTv_review_content.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {
        final TextView mTv_review_author;
        final TextView mTv_review_content;

        public ReviewHolder(View itemView) {
            super(itemView);
            mTv_review_author = (TextView) itemView.findViewById(R.id.tv_review_author);
            mTv_review_content = (TextView) itemView.findViewById(R.id.tv_review_content);
        }
    }
}
