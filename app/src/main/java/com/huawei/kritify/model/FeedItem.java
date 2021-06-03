package com.huawei.kritify.model;

import android.location.Location;
import java.time.LocalTime;
import java.util.ArrayList;

public class FeedItem {
    private String userName;
    private String entityName;
    private Location entityLocation;
    private LocalTime time;
    private ArrayList<String> imageUrls;
    private String review;

    public FeedItem(String userName, String entityName, Location entityLocation, LocalTime time, ArrayList<String> imageUrls, String review) {
        this.userName = userName;
        this.entityName = entityName;
        this.entityLocation = entityLocation;
        this.time = time;
        this.imageUrls = imageUrls;
        this.review = review;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Location getEntityLocation() {
        return entityLocation;
    }

    public void setEntityLocation(Location entityLocation) {
        this.entityLocation = entityLocation;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
