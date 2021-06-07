package com.huawei.kritify.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Site implements Serializable {
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private String type;
    @SerializedName("location")
    private LocationCoordinate location;

    public Site(long id, String name, String type, LocationCoordinate location) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
    }

    public Site(String name, String type, LocationCoordinate location) {
        this.name = name;
        this.type = type;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocationCoordinate getLocation() {
        return location;
    }

    public void setLocation(LocationCoordinate location) {
        this.location = location;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
