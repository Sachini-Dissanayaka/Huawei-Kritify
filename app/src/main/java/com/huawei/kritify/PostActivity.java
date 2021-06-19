package com.huawei.kritify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.huawei.kritify.model.Post;
import com.huawei.kritify.model.Site;
import com.huawei.kritify.retrofit.RetrofitInstance;
import com.huawei.kritify.retrofit.RetrofitInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.huawei.kritify.MainActivity.MY_PREFS_NAME;

public class PostActivity extends AppCompatActivity{

    public static final String TAG = "PostActivity";

    public String filePath;

    FirebaseStorage storage;
    StorageReference storageRef;

    //UI views
    private ImageSwitcher imagesPost;
    private MaterialButton btnSubmit;
    private ImageButton btnPick, btnNext, btnPrevious;
    private EditText username,description;
    private AutoCompleteTextView shop_name;
    private Toolbar toolbar;
    private Site selectedSite;
    private ConstraintLayout constraintLayout;
    private TextView errorUsername, errorSite, errorReview;
    private String user_token;
    private LinearProgressIndicator progressIndicator;

    //store image urls in this array list
    private ArrayList<Uri> imageUris;
    private ArrayList<String> imageUrls;

    //request code to pick images
    private static final int PICK_IMAGES_CODE = 0;

    //position of selected image
    int position = 0;

    // retrofit to call REST API
    RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //init UI views
        constraintLayout = findViewById(R.id.parent);
        username = findViewById(R.id.username);
        description = findViewById(R.id.description);
        imagesPost = findViewById(R.id.imagesPost);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnPick = findViewById(R.id.btnPick);
        shop_name = findViewById(R.id.autocomplete_shop_name);
        toolbar = findViewById(R.id.toolbar);
        errorUsername = findViewById(R.id.errorUsername);
        errorSite = findViewById(R.id.errorSite);
        errorReview = findViewById(R.id.errorReview);
        progressIndicator = findViewById(R.id.linear_progress);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user_token = prefs.getString(getString(R.string.kritify_key), "No token defined");
        //get tool bar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        //init list
        imageUris = new ArrayList<>();
        imageUrls = new ArrayList<>();

        //setup image switcher
        imagesPost.setFactory(() -> new ImageView(getApplicationContext()));

        // search shop names
        initSearch();

        //click handle, pick images
        btnPick.setOnClickListener(v -> pickImagesIntent());

        //click handle, show previous image
        btnPrevious.setOnClickListener(v -> {
            if (position > 0){
                position--;
                imagesPost.setImageURI(imageUris.get(position));
            }
            else {
                Toast.makeText(PostActivity.this,"No previous images...",Toast.LENGTH_SHORT).show();
            }
        });

        //click handle, show next image
        btnNext.setOnClickListener(v -> {
            if (position < imageUris.size() - 1){
                position++;
                imagesPost.setImageURI(imageUris.get(position));
            }
            else {
                Toast.makeText(PostActivity.this,"No more images...",Toast.LENGTH_SHORT).show();
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
            errorSite.setVisibility(View.GONE);
            selectedSite = (Site)parent.getItemAtPosition(position);
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
                if (data != null && data.getClipData() != null){
                    //picked multiple images

                    int cout= data.getClipData().getItemCount(); //number of picked images
                    for (int i=0; i<cout; i++){
                        //get image uri at specific index
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                    }
                    //set first image to our image switcher
                    imagesPost.setImageURI(imageUris.get(0));
                    position = 0;
                }
                else {
                    //picked single image
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);
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
            for (Uri uri: imageUris) {
                saveImage(uri);
            }
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
        if (selectedSite == null) {
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

    private void savePost(Post post){
        Call<Void> call = retrofitInterface.createPost(post);
        Log.d(TAG, post.getUserToken());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                finish();
                startActivity(new Intent(getApplicationContext(), FeedActivity.class));
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
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

    private void saveImage(Uri image) {
        progressIndicator.setVisibility(View.VISIBLE);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        StorageReference ref = storageRef.child("images/"+ UUID.randomUUID().toString());
        Log.d("ImagesUpload", ref.toString());
        UploadTask uploadTask = ref.putFile(image);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("ImagesUpload", exception.toString());
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("ImagesUpload", "uploaded");
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL

                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            if (downloadUri != null) {
                                imageUrls.add(downloadUri.toString());
                                Log.d("ImagesUpload", downloadUri.toString());
                                if (imageUris.size() == imageUrls.size()) {
                                    Post post = new Post(user_token,username.getText().toString(),selectedSite,
                                            imageUrls,description.getText().toString());
                                    progressIndicator.setVisibility(View.GONE);
                                    savePost(post);
                                }
                            }

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }
}