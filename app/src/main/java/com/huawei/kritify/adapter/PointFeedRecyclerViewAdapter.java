package com.huawei.kritify.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.kritify.R;
import com.huawei.kritify.model.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PointFeedRecyclerViewAdapter extends RecyclerView.Adapter<PointFeedRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "PointFeedRecyclerViewAdapter";

    private ArrayList<Post> posts;
    private Context context;

    // data is passed into the constructor
    public PointFeedRecyclerViewAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_point,parent,false);
        return new ViewHolder(v);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull PointFeedRecyclerViewAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Called");
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewShop;
        private TextView textViewDesc;
        private TextView time;

        ViewHolder(View itemView) {
            super(itemView);
            textViewShop = itemView.findViewById(R.id.textViewShop);
            textViewDesc = itemView.findViewById(R.id.textViewDesc);
            time = itemView.findViewById(R.id.time);
        }
        public void bind(final Post currentItem) {
            textViewShop.setText(currentItem.getSite().getName());

            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM d", Locale.getDefault());
            time.setText(dateFormatter.format(currentItem.getTime()));

            textViewDesc.setText(currentItem.getReview());
        }
    }
}
