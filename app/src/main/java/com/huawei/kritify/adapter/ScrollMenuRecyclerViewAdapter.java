package com.huawei.kritify.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.huawei.kritify.R;

import java.util.ArrayList;

public class ScrollMenuRecyclerViewAdapter extends RecyclerView.Adapter<ScrollMenuRecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "MenuRecyclerViewAdapter";

    private ArrayList<String> menuItems;
    private Context mContext;

    // data is passed into the constructor
    public ScrollMenuRecyclerViewAdapter(Context context, ArrayList<String> menuItems) {
        this.mContext = context;
        this.menuItems = menuItems;
    }

    // inflates the row layout from xml when needed, returns view holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menu, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to each item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Called");
        holder.parent.setText(menuItems.get(position));

        // on click see more
//        holder.seeMoreButton.setOnClickListener(v -> {
//            Intent intent = new Intent(mContext, FarmActivity.class);
//            intent.putExtra(FARM_KEY, farms.get(position).getId());
//            mContext.startActivity(intent);
//        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return menuItems.size();
    }


    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialButton parent;

        ViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.menuButton);
        }
    }
}
