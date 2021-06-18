package com.huawei.kritify;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.huawei.kritify.adapter.MainFeedRecyclerViewAdapter;
import com.huawei.kritify.model.Post;
import com.huawei.kritify.model.Site;
import com.huawei.kritify.retrofit.RetrofitInstance;
import com.huawei.kritify.retrofit.RetrofitInterface;
import com.huawei.kritify.adapter.PointFeedRecyclerViewAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.huawei.kritify.MainActivity.MY_PREFS_NAME;

public class PointActivity extends AppCompatActivity implements Serializable {

    public static final String TAG = "PointActivity";

    private String user_token;
    AutoCompleteTextView search;
    RecyclerView pointRecyclerView;
    PointFeedRecyclerViewAdapter pointFeedRecyclerViewAdapter;
    Toolbar toolbar;
    Site selectedSite;
    TextView shopCount;
    CircularProgressIndicator progressBar;
    ImageView errorImage;
    // retrofit to call REST API
    RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        toolbar = findViewById(R.id.toolbar);
        pointRecyclerView = findViewById((R.id.pointRecyclerView));
        search = findViewById(R.id.search);
        shopCount = findViewById(R.id.shops_count);
        progressBar = findViewById(R.id.progress_bar);
        errorImage = findViewById(R.id.errorImage);

        //get shared preference values
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user_token = prefs.getString(getString(R.string.kritify_key), "None");
        if (user_token.equals("None")){
            Toast.makeText(this,"No token defined",Toast.LENGTH_LONG).show();
        }

        //get tool bar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        pointRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // get all posts
        getInitialData();

        //initial search
        initSearch();
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
        pointFeedRecyclerViewAdapter = new PointFeedRecyclerViewAdapter(this, (ArrayList<Post>) body);
        pointRecyclerView.setAdapter(pointFeedRecyclerViewAdapter);
        pointFeedRecyclerViewAdapter.notifyDataSetChanged();
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
                getFilteredSites(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        search.setOnItemClickListener((parent, view, position, id) -> {
            selectedSite = (Site)parent.getItemAtPosition(position);
            getFilteredPostsBySiteAndToken(selectedSite.getId());
            Log.d("Search", String.valueOf(selectedSite.getId()));
        });

    }

    private void getFilteredSites(String siteName) {
        if (search.getText().toString().equals("")){
            shopCount.setVisibility(View.GONE);
            getInitialData();
        }
        // get filtered data
        Call<List<Site>> listCall = retrofitInterface.getSitesByName(siteName);
        listCall.enqueue(new Callback<List<Site>>() {
            @Override
            public void onResponse(@NonNull Call<List<Site>> call, @NonNull Response<List<Site>> response) {
                if (response.body() != null) {
                    ArrayAdapter<Site> adapter = new ArrayAdapter<>(PointActivity.this,
                            R.layout.site_search_item, response.body());
                    search.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Site>> call, @NonNull Throwable t) {
                Toast.makeText(PointActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get all posts when first initialized according to the token
    private void getInitialData() {
        // get data
        Call<List<Post>> listCall = retrofitInterface.getPostsByToken(user_token);
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
                Toast.makeText(PointActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get filtered posts of selected site
    private void getFilteredPostsBySiteAndToken(long id) {
        // get filtered data
        Call<List<Post>> listCall = retrofitInterface.getPostsByTokenAndSiteId(user_token,id);
        progressBar.setVisibility(View.VISIBLE);
        listCall.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                progressBar.setVisibility(View.GONE);
                hideErrorImage();
                parseData(response.body());
                if (response.body() != null) {
                    int count = response.body().size();
                    shopCount.setText("Your Points : "+ String.valueOf(count));
                    shopCount.setVisibility(View.VISIBLE);
                    Log.d("Search", response.body().toString());
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                showErrorImage();
//                Toast.makeText(PointActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showErrorImage() {
        pointRecyclerView.setVisibility(View.GONE);
        errorImage.setVisibility(View.VISIBLE);
    }

    public void hideErrorImage() {
        errorImage.setVisibility(View.GONE);
        pointRecyclerView.setVisibility(View.VISIBLE);
    }
}
