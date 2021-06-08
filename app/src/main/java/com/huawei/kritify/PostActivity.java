package com.huawei.kritify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.widget.ViewSwitcher;

import com.google.android.material.snackbar.Snackbar;
import com.huawei.kritify.model.Post;
import com.huawei.kritify.model.Site;
import com.huawei.kritify.retrofit.RetrofitInstance;
import com.huawei.kritify.retrofit.RetrofitInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {

    public static final String TAG = "PostActivity";

    //UI views
    private ImageSwitcher imagesPost;
    private Button btnSubmit, btnPrevious, btnNext, btnPick;
    private EditText username,description;
    private AutoCompleteTextView shop_name;
    private Toolbar toolbar;
    private Site selectedSite;
    private ConstraintLayout constraintLayout;
    private TextView errorUsername, errorSite, errorReview;

    //store image urls in this array list
    private ArrayList<Uri> imageUris;
    private ArrayList<String> imageUrls;

    //request code to pick images
    private static final int PICK_IMAGES_CODE = 0;

    //position of selected image
    int position = 0;

    // retrofit to call REST API
    RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //init UI views
        constraintLayout = findViewById(R.id.parent);
        username = (EditText) findViewById(R.id.username);
        description = (EditText) findViewById(R.id.description);
        imagesPost = (ImageSwitcher) findViewById(R.id.imagesPost);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPick = (Button) findViewById(R.id.btnPick);
        shop_name = (AutoCompleteTextView) findViewById(R.id.autocomplete_shop_name);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        errorUsername = (TextView) findViewById(R.id.errorUsername);
        errorSite = (TextView) findViewById(R.id.errorSite);
        errorReview = (TextView) findViewById(R.id.errorReview);

        //get tool bar
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        //init list
        imageUris = new ArrayList<>();
        imageUrls = new ArrayList<>();

        //setup image switcher
        imagesPost.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });

        // search shop names
        initSearch();

        //click handle, pick images
        btnPick.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pickImagesIntent();
            }
        });

        //click handle, show previous image
        btnPrevious.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (position > 0){
                    position--;
                    imagesPost.setImageURI(imageUris.get(position));
                }
                else {
                    Toast.makeText(PostActivity.this,"No Previous images...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //click handle, show next image
        btnNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (position < imageUris.size() - 1){
                    position++;
                    imagesPost.setImageURI(imageUris.get(position));
                }
                else {
                    Toast.makeText(PostActivity.this,"No More images...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSubmit.setOnClickListener(v -> saveSite());

    }

    private void initSearch() {
        shop_name.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        shop_name.addTextChangedListener(new TextWatcher() {

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

        shop_name.setOnItemClickListener((parent, view, position, id) -> {
            selectedSite = (Site)parent.getItemAtPosition(position);
            Log.d("Search", String.valueOf(selectedSite.getId()));
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
                    ArrayAdapter<Site> adapter = new ArrayAdapter<>(PostActivity.this,
                            R.layout.site_search_item, response.body());
                    shop_name.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Site>> call, @NonNull Throwable t) {
                Toast.makeText(PostActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickImagesIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image(s)"),PICK_IMAGES_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_CODE){
            if (resultCode == Activity.RESULT_OK){
                if (data.getClipData() != null){
                    //picked multiple images

                    int cout= data.getClipData().getItemCount(); //number of picked images
                    for (int i=0; i<cout; i++){
                        //get image uri at specific index
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                        imageUrls.add(imageUri.toString());
                    }
                    //set first image to our image switcher
                    imagesPost.setImageURI(imageUris.get(0));
                    position = 0;
                }
                else {
                    //picked single image
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);
                    imageUrls.add(imageUri.toString());
                    //set image to our image switcher
                    imagesPost.setImageURI(imageUris.get(0));
                    position = 0;
                }
            }
        }
    }

    private void saveSite() {
        if (validateData()) {
            errorUsername.setVisibility(View.GONE);
            errorSite.setVisibility(View.GONE);
            errorReview.setVisibility(View.GONE);

            Post post = new Post(username.getText().toString(),selectedSite,
                    imageUrls,description.getText().toString());
            savePost(post);
        }
    }

    private boolean validateData() {
        boolean errors = true;

        if (username.getText().toString().trim().equals("")) {
            errorUsername.setVisibility(View.VISIBLE);
            errors = false;
        } else {
            errorUsername.setVisibility(View.GONE);
        }
        if (shop_name.getText().toString().equals("")) {
            errorSite.setVisibility(View.VISIBLE);
            errors = false;
        } else {
            errorSite.setVisibility(View.GONE);
        }
        if (description.getText().toString().equals("")) {
            errorReview.setVisibility(View.VISIBLE);
            errors = false;
        } else {
            errorReview.setVisibility(View.GONE);
        }
        return errors;
    }

    private void showSnackBar() {
        Snackbar.make(constraintLayout, "Post saved successfully", Snackbar.LENGTH_LONG).show();
    }

    private void savePost(Post post){
        Call<Void> call = retrofitInterface.createPost(post);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                finish();
                startActivity(new Intent(getApplicationContext(), FeedActivity.class));
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.d(TAG, t.toString());
                call.cancel();
                AlertDialog dialog = new AlertDialog.Builder(PostActivity.this)
                        .setTitle("")
                        .setMessage("Oops! Something is wrong\n\nPost was not saved")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                        })
                        .create();
                dialog.setOnShowListener(arg0 ->
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#5E75F6")));

                dialog.show();
            }
        });
    }
}
