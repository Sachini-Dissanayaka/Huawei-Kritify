package com.huawei.kritify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapFragment;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.kritify.enums.EntityType;
import com.huawei.kritify.model.LocationCoordinate;
import com.huawei.kritify.model.Site;
import com.huawei.kritify.retrofit.RetrofitInstance;
import com.huawei.kritify.retrofit.RetrofitInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SiteActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "SiteActivity";

    Toolbar toolbar;
    ConstraintLayout constraintLayout;
    TextView siteNameError;
    EditText siteNameInput;
    TextView siteTypeError;
    AutoCompleteTextView siteTypeInput;
    TextView locationLatitude;
    TextView locationLongitude;
    TextView locationError;
    MaterialButton save;

    LocationCoordinate locationCoordinate;

    private HuaweiMap hMap;
    private MapFragment mMapFragment;

    // retrofit to call REST API
    RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);

        toolbar = findViewById(R.id.toolbar);
        constraintLayout = findViewById(R.id.parent);
        siteNameError = findViewById(R.id.site_name_error);
        siteNameInput = findViewById(R.id.site_name_edit);
        siteTypeError = findViewById(R.id.site_type_error);
        siteTypeInput = findViewById(R.id.autoComplete);
        locationLatitude = findViewById(R.id.location_latitude);
        locationLongitude = findViewById(R.id.location_longitude);
        locationError = findViewById(R.id.site_location_error);
        save = findViewById(R.id.saveButton);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        save.setOnClickListener(v -> saveSite());

        String[] siteTypeMenu = new String[] {EntityType.HOTEL, EntityType.RESTAURANT, EntityType.CLOTHING_STORE};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.site_type_list_item, siteTypeMenu);
        siteTypeInput.setAdapter(adapter);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap = huaweiMap;
        hMap.setMyLocationEnabled(true);
        hMap.setOnMapClickListener(latLng -> {
            locationLatitude.setVisibility(View.VISIBLE);
            String latitude = "Latitude: " + latLng.latitude;
            locationLatitude.setText(latitude);

            locationLongitude.setVisibility(View.VISIBLE);
            String longitude = "Longitude: " + latLng.longitude;
            locationLongitude.setText(longitude);

            locationCoordinate = new LocationCoordinate(latLng.latitude, latLng.longitude);
        });
    }

    private void saveSite() {
        if (validateData()) {
            siteNameError.setVisibility(View.GONE);
            siteTypeError.setVisibility(View.GONE);
            locationError.setVisibility(View.GONE);

            Site site = new Site(siteNameInput.getText().toString().trim(),
                    siteTypeInput.getText().toString(),
                    locationCoordinate);
            saveData(site);
        }
    }

    private boolean validateData() {
        boolean errors = true;

        if (siteNameInput.getText().toString().trim().equals("")) {
            siteNameError.setVisibility(View.VISIBLE);
            errors = false;
        } else {
            siteNameError.setVisibility(View.GONE);
        }
        if (siteTypeInput.getText().toString().equals("")) {
            siteTypeError.setVisibility(View.VISIBLE);
            errors = false;
        } else {
            siteTypeError.setVisibility(View.GONE);
        }
        if (locationLatitude.getText().toString().equals("")) {
            locationError.setVisibility(View.VISIBLE);
            errors = false;
        } else {
            locationError.setVisibility(View.GONE);
        }
        return errors;
    }

    private void showSnackBar() {
        Snackbar.make(constraintLayout, "Site saved successfully", Snackbar.LENGTH_LONG).show();
    }

    private void saveData(Site site) {
        Log.d(TAG, site.getName());
        Call<Void> call = retrofitInterface.createSite(site);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                showSnackBar();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.d(TAG, t.toString());
                call.cancel();
                new AlertDialog.Builder(SiteActivity.this)
                        .setTitle("Warning")
                        .setMessage("Site was not saved")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                        })
                        .create()
                        .show();
            }
        });
    }




}