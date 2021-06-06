package com.huawei.kritify;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity{

    //UI views
    private Spinner spinner_category;
    private ImageSwitcher imagesPost;
    private Button btnSubmit, btnPrevious, btnNext, btnPick;

    //store image urls in this array list
    private ArrayList<Uri> imageUris;

    //request code to pick images
    private static final int PICK_IMAGES_CODE = 0;

    //position of selected image
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //init UI views
        imagesPost = (ImageSwitcher) findViewById(R.id.imagesPost);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPick = (Button) findViewById(R.id.btnPick);

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

        addListenerOnButton();
        addListenerOnSpinnerItemSelection();

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

    public void addListenerOnSpinnerItemSelection() {
        spinner_category = (Spinner) findViewById(R.id.spinner_category);
        spinner_category.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

        spinner_category = (Spinner) findViewById(R.id.spinner_category);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(PostActivity.this,
                        "OnClickListener : " +
                                "\nspinner_category : "+ String.valueOf(spinner_category.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
            }

        });
    }
}
