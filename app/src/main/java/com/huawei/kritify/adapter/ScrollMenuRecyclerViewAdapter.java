package com.huawei.kritify.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.huawei.kritify.R;

import java.util.ArrayList;

public class ScrollMenuRecyclerViewAdapter extends RecyclerView.Adapter<ScrollMenuRecyclerViewAdapter.ViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    private static final String TAG = "MenuRecyclerViewAdapter";

    private ArrayList<String> menuItems;
    private Context mContext;
    private final OnItemClickListener listener;
    private int selectedPosition = 0;

    // data is passed into the constructor
    public ScrollMenuRecyclerViewAdapter(Context context, ArrayList<String> menuItems, OnItemClickListener listener) {
        this.mContext = context;
        this.menuItems = menuItems;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed, returns view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menu, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to each item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Called");
        if (selectedPosition == position) {
            holder.parent.setBackgroundColor(Color.parseColor("#03295E"));
        } else {
            holder.parent.setBackgroundColor(Color.parseColor("#5E75F6"));
        }
        holder.bind(menuItems.get(position), listener);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return menuItems.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        private MaterialButton parent;

        ViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.menuButton);
        }

        public void bind(final String item, final OnItemClickListener listener) {
            parent.setText(item);
            itemView.setOnClickListener(v -> {
                selectedPosition = getAdapterPosition();
                listener.onItemClick(item);
                notifyDataSetChanged();
            });
        }
    }
}
