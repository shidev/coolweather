package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Airnowcity;
import com.coolweather.android.gson.Daliy;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Life;
import com.coolweather.android.gson.Lifestyle;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private LinearLayout airnowcityLayout;

    private LinearLayout lifeLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private  TextView airqualityText;

    private ImageView bingPicImg;

    private String mWeatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        lifeLayout=(LinearLayout)findViewById(R.id.life_layout);
        airnowcityLayout=(LinearLayout)findViewById(R.id.airnowcity_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        airqualityText=(TextView)findViewById(R.id.airquality_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        String bingPic = prefs.getString("bing_pic", null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
        String daliyString=prefs.getString("daliy",null);
        if(daliyString!=null){
            Daliy daliy=Utility.handleDaliyResponse(daliyString);
            showDaliyInfo(daliy);
        }else{
            String weatherId=getIntent().getStringExtra("weather_id");
            forecastLayout.setVisibility(View.INVISIBLE);
            requestDaliy(weatherId);
        }
        String airnowcityString=prefs.getString("airnowcity",null);
        if(airnowcityString!=null){
            Airnowcity airnowcity=Utility.handleAirnowcityResponse(airnowcityString);
            showAirnowcityInfo(airnowcity);
        }else{
            String weatherId=getIntent().getStringExtra("weather_id");
            airnowcityLayout.setVisibility(View.INVISIBLE);
            requestAirnowcity(weatherId);
        }
        String lifeString=prefs.getString("life",null);
        if(lifeString!=null){
            Life life=Utility.handleLifeResponse(lifeString);
            showLifeInfo(life);
        }else{
            String weatherId=getIntent().getStringExtra("weather_id");
            requestLife(weatherId);
        }
    }

    public void requestWeather(final String weatherId){
        String weatherUrl="https://api.heweather.net/s6/weather/now?location="+weatherId+"&key=31882ef16d0447408183a7b4213bd172";
        HttpUtil.sendOKHttpRequest(weatherUrl, new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        loadBingPic();
    }

    public void requestDaliy(final String weatherId){
        String daliyUrl="https://api.heweather.net/s6/weather/forecast?location="+weatherId+"&key=31882ef16d0447408183a7b4213bd172";
        HttpUtil.sendOKHttpRequest(daliyUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Daliy daliy=Utility.handleDaliyResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(daliy!=null&&"ok".equals(daliy.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("daliy",responseText);
                            editor.apply();
                            showDaliyInfo(daliy);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取未来几日天气失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取未来几日天气失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void requestAirnowcity(final String weatherId){
        final String airnowcityUrl="https://api.heweather.net/s6/air/now?location="+weatherId+"&key=31882ef16d0447408183a7b4213bd172";
        HttpUtil.sendOKHttpRequest(airnowcityUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Airnowcity airnowcity=Utility.handleAirnowcityResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(airnowcity!=null&&"ok".equals(airnowcity.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("airnowcity",responseText);
                            editor.apply();
                            showAirnowcityInfo(airnowcity);
                        }else{
                            Toast.makeText(WeatherActivity.this,airnowcity.status,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取空气质量失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void requestLife(final String weatherId){
        String lifeUrl="https://api.heweather.net/s6/weather/lifestyle?location="+weatherId+"&key=31882ef16d0447408183a7b4213bd172";
        HttpUtil.sendOKHttpRequest(lifeUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Life life=Utility.handleLifeResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(life!=null&&"ok".equals(life.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("life",responseText);
                            editor.apply();
                            showLifeInfo(life);
                        }else{
                            Toast.makeText(WeatherActivity.this,life.status,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取生活建议失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOKHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
    private void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;
        String updateTime=weather.update.time.split(" ")[1];
        String degree=weather.now.temperature+"°C";
        String weatherInfo=weather.now.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        weatherInfoText.setText(weatherInfo);
        degreeText.setText(degree);
        weatherLayout.setVisibility(View.VISIBLE);
    }
    private void showDaliyInfo(Daliy daliy){
        forecastLayout.removeAllViews();
        for(Forecast forecast:daliy.forecastList){
            View view=LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText("日间："+forecast.daycondition+" "+"晚间："+forecast.nightcondition);
            maxText.setText(forecast.maxtempe);
            minText.setText(forecast.mintempe);
            forecastLayout.addView(view);
            forecastLayout.setVisibility(View.VISIBLE);
        }
    }
    private void showLifeInfo(Life life){
        lifeLayout.removeAllViews();
        for(Lifestyle lifestyle:life.lifeList){
            View view=LayoutInflater.from(this).inflate(R.layout.lifestyle,lifeLayout,false);
            TextView brifeText=(TextView)view.findViewById(R.id.brife_text);
            TextView textText=(TextView)view.findViewById(R.id.text_text);
            TextView typeText=(TextView)view.findViewById(R.id.type_text);
            String brife="概况："+lifestyle.brife;
            String text="实况："+lifestyle.text;
            brifeText.setText(brife);
            textText.setText(text);
            if("comf".equals(lifestyle.type)) {
                typeText.setText("舒适度指数：");
            }else if("cw".equals(lifestyle.type)){
                typeText.setText("洗车指数：");
            }else if("drsg".equals(lifestyle.type)){
                typeText.setText("很实用的穿衣指数：");
            }else if("flu".equals(lifestyle.type)){
                typeText.setText("感冒指数：");
            }else if("sport".equals(lifestyle.type)){
                typeText.setText("没在健身房办卡的你需要的运动指数：");
            }else if("trav".equals(lifestyle.type)){
                typeText.setText("旅游指数：");
            }else if("uv".equals(lifestyle.type)){
                typeText.setText("水晶女孩和水晶男孩想知道的紫外线指数:");
            }else if("air".equals(lifestyle.type)){
                typeText.setText("一项你可能并不关注的空气污染扩散条件指数：");
            }else if("ac".equals(lifestyle.type)){
                typeText.setText("空调开启指数：");
            }else if("gl".equals(lifestyle.type)){
                typeText.setText("太阳镜指数：");
            }else if("mu".equals(lifestyle.type)){
                typeText.setText("化妆指数：");
            }else if("airc".equals(lifestyle.type)){
                typeText.setText("晾晒指数：");
            }else if("ptfc".equals(lifestyle.type)){
                typeText.setText("交通指数：");
            }else if("fsh".equals(lifestyle.type)){
                typeText.setText("钓鱼指数：");
            }else{
                typeText.setText("防晒指数：");
            }

            lifeLayout.addView(view);
            lifeLayout.setVisibility(View.VISIBLE);
        }
    }
    private void showAirnowcityInfo(Airnowcity airnowcity){
        String aqi=airnowcity.airnow.aqi;
        String pm25=airnowcity.airnow.pm25;
        aqiText.setText(aqi);
        pm25Text.setText(pm25);
        String Airquality = airnowcity.airnow.Airquality;
        airqualityText.setText("空气质量:" + Airquality);
        airnowcityLayout.setVisibility(View.VISIBLE);
    }
}
