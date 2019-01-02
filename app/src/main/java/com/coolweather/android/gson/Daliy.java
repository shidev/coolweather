package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Daliy {
    public String status;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
