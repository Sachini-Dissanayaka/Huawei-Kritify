package com.huawei.kritify.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.huawei.kritify.R;

import java.util.ArrayList;

public class FeedImageRecyclerViewAdapter extends RecyclerView.Adapter<FeedImageRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "ImageRecyclerAdapter";

    private ArrayList<String> feedImages;
    private Context mContext;

    // data is passed into the constructor
    public FeedImageRecyclerViewAdapter(Context context, ArrayList<String> feedImages) {
        this.mContext = context;
        this.feedImages = feedImages;
    }

    // inflates the row layout from xml when needed, returns view holder
    @Override
    public FeedImageRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_feed_image, parent, false);
        return new FeedImageRecyclerViewAdapter.ViewHolder(view);
    }

    // binds the data to each feed image
    @Override
    public void onBindViewHolder(FeedImageRecyclerViewAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Called");
        Glide.with(mContext)
                .asBitmap()
                .load(feedImages.get(position))
                .transform(new RoundedCorners(30))
                .into(holder.image);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return feedImages.size();
    }


    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.feedImage);
        }
    }
}
