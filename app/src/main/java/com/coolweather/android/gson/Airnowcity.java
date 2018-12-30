package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Airnowcity {
    public String aqi;
    public String pm25;

    @SerializedName("qlty")
    public String Airquality;
}
