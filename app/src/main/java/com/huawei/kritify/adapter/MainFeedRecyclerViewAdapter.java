package com.huawei.kritify.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huawei.kritify.R;
import com.huawei.kritify.model.FeedItem;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MainFeedRecyclerViewAdapter extends RecyclerView.Adapter<MainFeedRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "FeedRecyclerViewAdapter";

    private ArrayList<FeedItem> feedItems;
    private Context mContext;

    // data is passed into the constructor
    public MainFeedRecyclerViewAdapter(Context context, ArrayList<FeedItem> feedItems) {
        this.mContext = context;
        this.feedItems = feedItems;
    }

    // inflates the row layout from xml when needed, returns view holder
    @Override
    public MainFeedRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_feed, parent, false);
        return new MainFeedRecyclerViewAdapter.ViewHolder(view);
    }

    // binds the data to each feed item
    @Override
    public void onBindViewHolder(MainFeedRecyclerViewAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Called");
        holder.username.setText(feedItems.get(position).getUserName());
        holder.entityName.setText(feedItems.get(position).getEntityName());
        //TODO: format correct time
        holder.time.setText(feedItems.get(position).getTime().toString());
        //TODO: give location coordinates to icon
        Glide.with(mContext)
                .asBitmap()
                .load(feedItems.get(position)
                        .getImageUrls().get(0))
                .transform(new RoundedCornersTransformation(30, 0))
                .into(holder.image);

        holder.review.setText(feedItems.get(position).getReview());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return feedItems.size();
    }


    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView entityName;
        private TextView time;
        private ImageButton location;
        private ImageView image;
        private TextView review;

        ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            entityName = itemView.findViewById(R.id.entity_name);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.location_icon);
            image = itemView.findViewById(R.id.image);
            review = itemView.findViewById(R.id.review);
        }
    }
}
