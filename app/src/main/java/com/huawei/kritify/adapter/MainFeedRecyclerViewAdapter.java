package com.huawei.kritify.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.huawei.kritify.R;
import com.huawei.kritify.model.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainFeedRecyclerViewAdapter extends RecyclerView.Adapter<MainFeedRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "FeedRecyclerViewAdapter";

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    private ArrayList<Post> posts;
    private Context mContext;
    private final OnItemClickListener listener;

    // data is passed into the constructor
    public MainFeedRecyclerViewAdapter(Context context, ArrayList<Post> posts, OnItemClickListener listener) {
        this.mContext = context;
        this.posts = posts;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed, returns view holder
    @NonNull
    @Override
    public MainFeedRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_feed, parent, false);
        return new MainFeedRecyclerViewAdapter.ViewHolder(view);
    }

    // binds the data to each feed item
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(MainFeedRecyclerViewAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Called");
        holder.bind(posts.get(position), listener);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return posts.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView entityName;
        private TextView time;
        private ImageButton location;
        private RecyclerView imageRecyclerView;
        private TextView review;

        ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            entityName = itemView.findViewById(R.id.entity_name);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.location_icon);
            imageRecyclerView = itemView.findViewById(R.id.imageRecyclerView);
            review = itemView.findViewById(R.id.review);
        }

        public void bind(final Post currentItem, final OnItemClickListener listener) {
            username.setText(currentItem.getUserName());
            entityName.setText(currentItem.getSite().getName());

            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM d", Locale.getDefault());
            time.setText(dateFormatter.format(currentItem.getTime()));

            review.setText(currentItem.getReview());

            imageRecyclerView.setLayoutManager(new LinearLayoutManager(
                    mContext, LinearLayoutManager.HORIZONTAL, false));
            imageRecyclerView.setHasFixedSize(true);

            FeedImageRecyclerViewAdapter feedImageRecyclerViewAdapter
                    = new FeedImageRecyclerViewAdapter(
                    mContext, currentItem.getImageUrls());

            imageRecyclerView.setAdapter(feedImageRecyclerViewAdapter);

            location.setOnClickListener(v -> {
                listener.onItemClick(currentItem);
                notifyDataSetChanged();
            });
        }
    }
}
