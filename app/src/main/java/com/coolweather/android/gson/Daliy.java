package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Daliy {
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
