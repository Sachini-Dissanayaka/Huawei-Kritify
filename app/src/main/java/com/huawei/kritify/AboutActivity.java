package com.huawei.kritify;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity  extends AppCompatActivity {

    private TextView aboutUs;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar = findViewById(R.id.toolbar);

        //get tool bar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        aboutUs = findViewById(R.id.aboutUs);
        String description = "We are Team SYsters from\n" +
                "University of Moratuwa, Sri Lanka.\n" +
                "\n" +
                "Contact us:\n" +
                "\n"+
                "Yoshani Ranaweera - yoshani.malinka@gmail.com\n" +
                "\n"+
                "Sachini Dissanayaka - dmsachiniacc@gmail.com";
        aboutUs.setText(description);

    }
}
