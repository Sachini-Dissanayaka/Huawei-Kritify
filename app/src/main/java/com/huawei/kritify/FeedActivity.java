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
import com.huawei.kritify.enums.EntityType;
import com.huawei.kritify.model.Entity;
import com.huawei.kritify.model.Post;

import java.time.LocalDateTime;
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
        Location loc = new Location("dummyprovider");
        loc.setLatitude(20.3);
        loc.setLongitude(52.6);
        Entity entity1 = new Entity(1, "Kingsbury Restaurant", EntityType.HOTEL, loc);
        Entity entity2 = new Entity(1, "Ciara Hotel", EntityType.RESTAURANT, loc);
        Entity entity3 = new Entity(1, "Symphony Hotel", EntityType.CLOTHING_STORE, loc);

        posts = new ArrayList<>(Arrays.asList(
                new Post(1,
                        "Yoshani Ranaweera",
                        entity1,
                        LocalDateTime.now(),
                        imageList,
                        "The food here is simply amazing! Go eat the world's most delicious food. You sure won't regret that!"),
                new Post(2,
                        "Sachini Dissanayaka",
                        entity2,
                        LocalDateTime.now(),
                        imageList,
                        "The food here is simply amazing! You should totally check it out"),
                new Post(3,
                        "Daphne Waters",
                        entity3,
                        LocalDateTime.now(),
                        imageList,
                        "The food here is simply amazing! You should totally check it out")
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