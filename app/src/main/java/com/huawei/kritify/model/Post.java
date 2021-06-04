package com.huawei.kritify.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Post {
    private int id;
    private String userName;
    private Entity entity;
    private LocalDateTime time;
    private ArrayList<String> imageUrls;
    private String review;

    public Post(int id, String userName, Entity entity, LocalDateTime time, ArrayList<String> imageUrls, String review) {
        this.id = id;
        this.userName = userName;
        this.entity = entity;
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

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
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
