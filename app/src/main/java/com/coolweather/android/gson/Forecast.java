package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    public String date;

    @SerializedName("tmp_max")
    public String maxtempe;

    @SerializedName("tmp_min")
    public String mintempe;

    @SerializedName("cond_txt_d")
    public String daycondition;

    @SerializedName("cond_txt_n")
    public String nightcondition;
}
