package com.huawei.kritify;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapFragment;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.kritify.model.Post;

import static com.huawei.kritify.FeedActivity.FEED_KEY;

public class FeedMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "FeedMapActivity";

    private static final String[] RUNTIME_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};

    private static final int REQUEST_CODE = 100;

    private HuaweiMap hMap;
    private MapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_map);
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(HuaweiMap map) {
        Log.d(TAG, "onMapReady: ");
        hMap = map;
        hMap.setMyLocationEnabled(true);
        hMap.getUiSettings().setMyLocationButtonEnabled(true);

        // display marker
        Intent intent = getIntent();
        if (null != intent) {
            Post post = (Post)getIntent().getSerializableExtra(FEED_KEY);
            Log.d(TAG, "onCreate: called");
            if (null != post) {
                Log.d(TAG, String.valueOf(post.getSite().getLocation().getLatitude()));
                Log.d(TAG, String.valueOf(post.getSite().getLocation().getLongitude()));
                LatLng mCoordinates = new LatLng(post.getSite().getLocation().getLatitude(),
                        post.getSite().getLocation().getLongitude());
                hMap.addMarker(new MarkerOptions().position(mCoordinates)
                        .anchorMarker(0.5f, 0.9f)
                        .title(post.getSite().getName()));
                hMap.setOnMapClickListener(latLng -> Log.d(TAG, latLng.toString()));
                hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCoordinates, 10));
                hMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        }
    }
}