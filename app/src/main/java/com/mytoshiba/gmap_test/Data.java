package com.mytoshiba.gmap_test;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    private String latitude;
    private String longitude;
    private String location;
    private String userName;

    public Data(String latitude, String longitude, String location, String userName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.userName = userName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

