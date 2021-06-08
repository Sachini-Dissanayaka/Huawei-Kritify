package com.huawei.kritify;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import android.widget.ViewSwitcher;

import com.huawei.kritify.retrofit.RetrofitInstance;
import com.huawei.kritify.retrofit.RetrofitInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {

    //UI views
//    private Spinner spinner_category;
    private ImageSwitcher imagesPost;
    private Button btnSubmit, btnPrevious, btnNext, btnPick;
    private EditText username,description;
    private AutoCompleteTextView shop_name;
    private Toolbar toolbar;

    //store image urls in this array list
    private ArrayList<Uri> imageUris;

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
        username = (EditText) findViewById(R.id.username);
        description = (EditText) findViewById(R.id.description);
        imagesPost = (ImageSwitcher) findViewById(R.id.imagesPost);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPick = (Button) findViewById(R.id.btnPick);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        // Get a reference to the AutoCompleteTextView in the layout
        shop_name = (AutoCompleteTextView) findViewById(R.id.autocomplete_shop_name);

        //init list
        imageUris = new ArrayList<>();

        //setup image switcher
        imagesPost.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });

        addListenerOnShopItemSelection();
        addListenerOnButton();

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

    //shop name selection
    public void addListenerOnShopItemSelection(){
        // Get the string array
        String[] shops = getResources().getStringArray(R.array.shops_array);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shops);
        shop_name.setAdapter(adapter);
    }

    public void addListenerOnButton() {

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(PostActivity.this,
                        "OnClickListener : " +
                                "\nuser_name : "+ username.getText().toString()+
                                "\nshop_name : "+ shop_name.getText().toString()+
                                "\ndescription : "+ description.getText().toString()+
                                "\nimages_urls : "+ imageUris.toString(),
                        Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(), FeedActivity.class));
            }

        });
    }
}