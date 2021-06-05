package com.huawei.kritify;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.huawei.kritify.adapter.MainFeedRecyclerViewAdapter;
import com.huawei.kritify.adapter.ScrollMenuRecyclerViewAdapter;
import com.huawei.kritify.model.Post;
import com.huawei.kritify.retrofit.RetrofitInstance;
import com.huawei.kritify.retrofit.RetrofitInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedActivity extends AppCompatActivity {

    public static final String TAG = "FeedActivity";

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
        setSupportActionBar(toolbar);

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

        // get data
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<List<Post>> listCall = retrofitInterface.getAllPosts();
        listCall.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                parseData(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                Toast.makeText(FeedActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

//        ArrayList<String> imageList = new ArrayList<>(Arrays.asList("https://nessarestaurant.com/wp-content/uploads/2018/11/Restaurant-Food.jpg", "https://cdn.designrulz.com/wp-content/uploads/2015/04/Joie-restaurant-_designrulz-1.jpg"));
//        Location loc = new Location("dummyprovider");
//        loc.setLatitude(20.3);
//        loc.setLongitude(52.6);
//        Site site1 = new Site(1, "Kingsbury Restaurant", EntityType.HOTEL, loc);
//        Site site2 = new Site(1, "Ciara Hotel", EntityType.RESTAURANT, loc);
//        Site site3 = new Site(1, "Symphony Hotel", EntityType.CLOTHING_STORE, loc);
//
//        posts = new ArrayList<>(Arrays.asList(
//                new Post(1,
//                        "Yoshani Ranaweera",
//                        site1,
//                        new Date(),
//                        imageList,
//                        "The food here is simply amazing! Go eat the world's most delicious food. You sure won't regret that!"),
//                new Post(2,
//                        "Sachini Dissanayaka",
//                        site2,
//                        new Date(),
//                        imageList,
//                        "The food here is simply amazing! You should totally check it out"),
//                new Post(3,
//                        "Daphne Waters",
//                        site3,
//                        new Date(),
//                        imageList,
//                        "The food here is simply amazing! You should totally check it out")
//        ));



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

    private void parseData(List<Post> body) {
        mainFeedRecyclerViewAdapter = new MainFeedRecyclerViewAdapter(this, (ArrayList<Post>) body);
        recyclerViewFeed.setAdapter(mainFeedRecyclerViewAdapter);
        mainFeedRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.toolbar_icons, popup.getMenu());
        popup.show();
    }
}