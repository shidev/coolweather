package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Life {
    public String status;
    @SerializedName("lifestyle")
    public List<Lifestyle> lifeList;
}
