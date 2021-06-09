package com.huawei.kritify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.huawei.kritify.adapter.MainFeedRecyclerViewAdapter;
import com.huawei.kritify.adapter.ScrollMenuRecyclerViewAdapter;
import com.huawei.kritify.enums.EntityType;
import com.huawei.kritify.enums.MenuType;
import com.huawei.kritify.model.Post;
import com.huawei.kritify.model.Site;
import com.huawei.kritify.retrofit.RetrofitInstance;
import com.huawei.kritify.retrofit.RetrofitInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    public static final String TAG = "FeedActivity";
    public static final String FEED_KEY = "feedKey";

    private String selectedMenuItem = "All";

    FloatingActionButton fab;
    AutoCompleteTextView search;
    RecyclerView recyclerViewMenu;
    ArrayList<String> menuItems;
    ScrollMenuRecyclerViewAdapter scrollMenuRecyclerViewAdapter;

    RecyclerView recyclerViewFeed;
    MainFeedRecyclerViewAdapter mainFeedRecyclerViewAdapter;
    ImageView errorImage;
    CircularProgressIndicator progressBar;

    // retrofit to call REST API
    RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);

    private Post locationClickedPost;
    private boolean clickedAddSite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PostActivity.class)));

        recyclerViewMenu = findViewById(R.id.menuRecyclerView);
        search = findViewById(R.id.search);
        recyclerViewFeed = findViewById(R.id.feedRecyclerView);
        errorImage = findViewById(R.id.errorImage);
        progressBar = findViewById(R.id.progress_bar);

        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));

        menuItems = new ArrayList<>(
                Arrays.asList(MenuType.ALL, MenuType.HOTELS, MenuType.FOOD, MenuType.CLOTHING));

        scrollMenuRecyclerViewAdapter = new ScrollMenuRecyclerViewAdapter(this, menuItems, item -> {
            if (!item.equals(selectedMenuItem)) {
                selectedMenuItem = item;
                search.setText("");
                Log.d("Search", selectedMenuItem);
                if (item.equals(MenuType.ALL)) {
                    getInitialData();
                } else {
                    getFilteredData(convertMenuTypeToEntityType(item));
                }
            }
        });
        recyclerViewMenu.setAdapter(scrollMenuRecyclerViewAdapter);

        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(this));

        // search
        initSearch();

        // get all posts
        getInitialData();
    }

    // make AutoCompleteTextView lose focus
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // sets data for main feed
    private void parseData(List<Post> body) {
        mainFeedRecyclerViewAdapter = new MainFeedRecyclerViewAdapter(this, (ArrayList<Post>) body, post -> {
            locationClickedPost = post;
            if (!clickedAddSite && checkLocationPermission()) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission. ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent locationIntent = new Intent(FeedActivity.this, FeedMapActivity.class);
                    locationIntent.putExtra(FEED_KEY, post);
                    startActivity(locationIntent);
                }
            }

        });
        recyclerViewFeed.setAdapter(mainFeedRecyclerViewAdapter);
        mainFeedRecyclerViewAdapter.notifyDataSetChanged();
    }

    // popup settings menu
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.toolbar_icons, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.add_site) {
            clickedAddSite = true;
            if (checkLocationPermission()) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission. ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(FeedActivity.this, SiteActivity.class);
                    startActivity(intent);
                    return true;
                }
            }
        }
        if (item.getItemId() == R.id.action_about_us) {
            Intent intent = new Intent(FeedActivity.this, AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSearch() {
        search.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectedMenuItem.equals("All")) {
                    getFilteredSites(s.toString());
                } else {
                    getFilteredSitesByType(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        search.setOnItemClickListener((parent, view, position, id) -> {
            Site selectedSite = (Site)parent.getItemAtPosition(position);
            getFilteredPostsBySite(selectedSite.getId());
            Log.d("Search", String.valueOf(selectedSite.getId()));
        });

    }

    // get all posts when first initialized
    private void getInitialData() {
        // get data
        Call<List<Post>> listCall = retrofitInterface.getAllPosts();
        listCall.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                progressBar.setVisibility(View.GONE);
                hideErrorImage();
                parseData(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                showErrorImage();
                Log.e(TAG,"Load Error:" + t.toString());
            }
        });
    }

    // get filtered posts according to menu button click
    private void getFilteredData(String type) {
        // get filtered data
        Call<List<Post>> listCall = retrofitInterface.getPostsBySiteType(type);
        progressBar.setVisibility(View.VISIBLE);
        listCall.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                progressBar.setVisibility(View.GONE);
                hideErrorImage();
                parseData(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                showErrorImage();
            }
        });
    }

    // get filtered sites by name: when "All" menu type is selected
    private void getFilteredSites(String siteName) {
        // get filtered data
        Call<List<Site>> listCall = retrofitInterface.getSitesByName(siteName);
        listCall.enqueue(new Callback<List<Site>>() {
            @Override
            public void onResponse(@NonNull Call<List<Site>> call, @NonNull Response<List<Site>> response) {
                if (response.body() != null) {
                    ArrayAdapter<Site> adapter = new ArrayAdapter<>(FeedActivity.this,
                            R.layout.site_search_item, response.body());
                    search.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Site>> call, @NonNull Throwable t) {
                Toast.makeText(FeedActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get filtered sites by name and type: when other menu type is selected
    private void getFilteredSitesByType(String siteName) {
        Log.d("Search", convertMenuTypeToEntityType(selectedMenuItem));
        Call<List<Site>> listCall = retrofitInterface.getSitesByTypeAndName
                (convertMenuTypeToEntityType(selectedMenuItem), siteName);
        listCall.enqueue(new Callback<List<Site>>() {
            @Override
            public void onResponse(@NonNull Call<List<Site>> call, @NonNull Response<List<Site>> response) {
                if (response.body() != null) {
                    Log.d("Search", response.body().toString());
                    ArrayAdapter<Site> adapter = new ArrayAdapter<>(FeedActivity.this,
                            R.layout.site_search_item, response.body());
                    search.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Site>> call, @NonNull Throwable t) {
                Toast.makeText(FeedActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get filtered posts of selected site
    private void getFilteredPostsBySite(long id) {
        // get filtered data
        Call<List<Post>> listCall = retrofitInterface.getPostsBySite(id);
        progressBar.setVisibility(View.VISIBLE);
        listCall.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                progressBar.setVisibility(View.GONE);
                hideErrorImage();
                parseData(response.body());
                if (response.body() != null) {
                    Log.d("Search", response.body().toString());
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                showErrorImage();
            }
        });
    }

    // get location permission
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (clickedAddSite) {
                        clickedAddSite = false;
                        Intent intent = new Intent(FeedActivity.this, SiteActivity.class);
                        startActivity(intent);
                    } else {
                        Intent locationIntent = new Intent(FeedActivity.this, FeedMapActivity.class);
                        locationIntent.putExtra(FEED_KEY, locationClickedPost);
                        startActivity(locationIntent);
                    }
                }
            } else {
                clickedAddSite = false;
                Toast.makeText(FeedActivity.this, "Cannot open Huawei Maps", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String convertMenuTypeToEntityType(String menuType) {
        switch (menuType) {
            case MenuType.HOTELS:
                return EntityType.HOTEL;
            case MenuType.FOOD:
                return EntityType.RESTAURANT;
            case MenuType.CLOTHING:
                return EntityType.CLOTHING_STORE;
            default:
                return "";
        }
    }

    public void showErrorImage() {
        recyclerViewFeed.setVisibility(View.GONE);
        errorImage.setVisibility(View.VISIBLE);
    }

    public void hideErrorImage() {
        errorImage.setVisibility(View.GONE);
        recyclerViewFeed.setVisibility(View.VISIBLE);
    }
}