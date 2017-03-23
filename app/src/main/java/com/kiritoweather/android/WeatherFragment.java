package com.kiritoweather.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kiritoweather.android.gson.Forecast;
import com.kiritoweather.android.gson.Weather;
import com.kiritoweather.android.service.AutoUpdateService;
import com.kiritoweather.android.util.HttpUtil;
import com.kiritoweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by ASUS on 2017/3/21.
 */

public class WeatherFragment extends Fragment{

    private Button navButton;

    private SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView apiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView dressText;

    private TextView sportText;

    private TextView tripText;

    private TextView uvText;

    public String mWeatherId;

    private int position;

    private Activity activity;

    public static WeatherFragment newInstance(String weatherId, int position){
        Bundle args = new Bundle();
        args.putString("weather", weatherId);
        args.putInt("page", position);
        WeatherFragment weatherFragment = new WeatherFragment();
        weatherFragment.setArguments(args);
        return weatherFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        System.out.println("onCreate  " + activity.toString());
        mWeatherId = getArguments().getString("weather");
        position = getArguments().getInt("page");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        weatherLayout = (ScrollView)view.findViewById(R.id.weather_layout);
        titleCity = (TextView)view.findViewById(R.id.title_city);
        titleUpdateTime = (TextView)view.findViewById(R.id.title_update_time);
        degreeText = (TextView)view.findViewById(R.id.degree_text);
        weatherInfoText = (TextView)view.findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout)view.findViewById(R.id.forecast_layout);
        apiText = (TextView)view.findViewById(R.id.aqi_text);
        pm25Text = (TextView)view.findViewById(R.id.pm25_text);
        comfortText = (TextView)view.findViewById(R.id.comfort_text);
        carWashText = (TextView)view.findViewById(R.id.car_wash_text);
        dressText = (TextView)view.findViewById(R.id.dress_clothes_text);
        sportText = (TextView)view.findViewById(R.id.sport_text);
        tripText = (TextView)view.findViewById(R.id.trip_text);
        uvText = (TextView)view.findViewById(R.id.uv_text);
        navButton = (Button)view.findViewById(R.id.nav_button);

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeatherActivity weatherActivity = (WeatherActivity)getActivity();
                weatherActivity.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        swipeRefresh = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null){
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else {
            // 无缓存时去服务器查询天气
            mWeatherId = getActivity().getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        return view;
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId){
        System.out.println(weatherId.toString());
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId +
                "&key=752518ccd72d4881acda1f2b5698ecde";
        HttpUtil.sendOKHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(getActivity().toString());
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(activity).edit();
                            if (position == 1){
                                editor.putString("weather2", responseText);
                            }else if (position == 2){
                                editor.putString("weather3", responseText);
                            }else {
                                editor.putString("weather", responseText);
                            }
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(getActivity(), "获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }


    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(activity).inflate(R.layout.forecast_item, forecastLayout,false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }

        if (weather.aqi != null){
            apiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String dress = "穿衣建议：" + weather.suggestion.dress.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        String trip = "旅游建议：" + weather.suggestion.trip.info;
        String uv = "紫外线强度：" + weather.suggestion.uv.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        dressText.setText(dress);
        sportText.setText(sport);
        tripText.setText(trip);
        uvText.setText(uv);
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(activity, AutoUpdateService.class);
        activity.startService(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("WeatherFragment: onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("WeatherFragment: onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("WeatherFragment: onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("WeatherFragment: onDestroy");

    }
}
