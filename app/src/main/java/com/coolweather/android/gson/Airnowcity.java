package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Airnowcity {

    public String status;

    @SerializedName("air_now_city")
    public Airnow airnow;

    public class Airnow {
        public String aqi;
        public String pm25;

        @SerializedName("qlty")
        public String Airquality;
    }
}
