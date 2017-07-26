package com.example.administrator.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.administrator.popularmovies.R;
import com.example.administrator.popularmovies.model.MovieTrailer;

import java.util.List;

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.TrailerHolder> {
    private final Context mContext;
    private final List<MovieTrailer.ResultsBean> mtrailerList;
    private final ItemClickListener mItemClickListener;


    public MovieTrailersAdapter(Context context, List<MovieTrailer.ResultsBean> trailerList, ItemClickListener itemClickListener) {
        mContext = context;
        mtrailerList = trailerList;
        mItemClickListener = itemClickListener;
    }

    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_trailers_row, parent, false);
        return new TrailerHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, int position) {
        if (mtrailerList.size() > 0) {
            MovieTrailer.ResultsBean trailer = mtrailerList.get(position);
            holder.mTv_trailerCount.setText("Trailer " + (position + 1));
            holder.mTv_trailerName.setText(trailer.getName());
        }
    }

    @Override
    public int getItemCount() {
        return mtrailerList.size();
    }

    class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mTv_trailerCount;
        final TextView mTv_trailerName;
        final ImageButton mImageButton;

        public TrailerHolder(View itemView) {
            super(itemView);
            mTv_trailerCount = (TextView) itemView.findViewById(R.id.tv_trailer_count);
            mTv_trailerName =(TextView) itemView.findViewById(R.id.tv_trailer_name);
            mImageButton = (ImageButton) itemView.findViewById(R.id.imgbtn_trailer);
            mImageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mItemClickListener.onItemClick(position);
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }
}
