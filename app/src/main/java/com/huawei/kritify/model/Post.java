package com.huawei.kritify.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Post implements Serializable {

    @SerializedName("id")
    private long id;
    @SerializedName("userName")
    private String userName;
    @SerializedName("site")
    private Site site;
    @SerializedName("time")
    private Date time;
    @SerializedName("images")
    private ArrayList<String> imageUrls;
    @SerializedName("review")
    private String review;

    public Post(long id, String userName, Site site, Date time, ArrayList<String> imageUrls, String review) {
        this.id = id;
        this.userName = userName;
        this.site = site;
        this.time = time;
        this.imageUrls = imageUrls;
        this.review = review;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
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
