package com.huawei.kritify;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import com.huawei.kritify.adapter.MainFeedRecyclerViewAdapter;
import com.huawei.kritify.adapter.ScrollMenuRecyclerViewAdapter;
import com.huawei.kritify.model.Post;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class FeedActivity extends AppCompatActivity {

    SearchView searchView;
    RecyclerView recyclerViewMenu;
    ArrayList<String> menuItems;
    ScrollMenuRecyclerViewAdapter scrollMenuRecyclerViewAdapter;

    RecyclerView recyclerViewFeed;
    ArrayList<Post> posts;
    MainFeedRecyclerViewAdapter mainFeedRecyclerViewAdapter;

    ArrayAdapter<String> adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);;

        recyclerViewMenu = findViewById(R.id.menuRecyclerView);
        searchView = findViewById(R.id.searchView);
        recyclerViewFeed = findViewById(R.id.feedRecyclerView);

        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));

        menuItems = new ArrayList<>(
                Arrays.asList("All", "Hotels", "Food", "Clothing"));

        scrollMenuRecyclerViewAdapter = new ScrollMenuRecyclerViewAdapter(this, menuItems);
        recyclerViewMenu.setAdapter(scrollMenuRecyclerViewAdapter);

        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> imageList = new ArrayList<>(Arrays.asList("https://nessarestaurant.com/wp-content/uploads/2018/11/Restaurant-Food.jpg", "https://cdn.designrulz.com/wp-content/uploads/2015/04/Joie-restaurant-_designrulz-1.jpg"));

        posts = new ArrayList<>(Arrays.asList(
                new Post("Yoshani Ranaweera",
                        "Kingsbury Restaurant",
                        new Location(""),
                        LocalTime.parse("12:32",
                                DateTimeFormatter.ISO_TIME),
                        imageList,
                        "The food here is simply amazing! You should totally check it out"),
                new Post("Sachini Dissanayake",
                        "Ciara Hotel",
                        new Location(""),
                        LocalTime.parse("11:30",
                                DateTimeFormatter.ISO_TIME),
                        imageList,
                        "I had a really wonderful time at this hotel! You should totally check it out"),
                new Post("Daphne Waters",
                        "Udawalawa Hotel",
                        new Location(""),
                        LocalTime.parse("10:38",
                                DateTimeFormatter.ISO_TIME),
                        imageList,
                        "I had a really wonderful time at this hotel! You should totally check it out")
        ));

        mainFeedRecyclerViewAdapter = new MainFeedRecyclerViewAdapter(this, posts);
        recyclerViewFeed.setAdapter(mainFeedRecyclerViewAdapter);
        mainFeedRecyclerViewAdapter.notifyDataSetChanged();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                if(menuItems.contains(query)){
//                    scrollMenuRecyclerViewAdapter.getFilter().filter(query);
//                }else{
//                    Toast.makeText(FeedActivity.this, "No Match found",Toast.LENGTH_LONG).show();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                //    adapter.getFilter().filter(newText);
//                return false;
//            }
//        });

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.toolbar_icons, popup.getMenu());
        popup.show();
    }
}