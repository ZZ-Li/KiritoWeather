package com.kiritoweather.android.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kiritoweather.android.ChooseAreaFragment;
import com.kiritoweather.android.WeatherFragment;
import com.kiritoweather.android.gson.Weather;
import com.kiritoweather.android.util.Utility;

/**
 * Created by ASUS on 2017/3/22.
 */

public class WeatherFragmentAdapter extends FragmentPagerAdapter{

    private Context context;
    private String weatherId;

    public WeatherFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }


    @Override
    public Fragment getItem(int position) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String firstWeather = prefs.getString("weather", null);
        String secondWeather = prefs.getString("weather2", null);
        String thirdWeather = prefs.getString("weather3", null);
        if (position == 1){
            if (secondWeather != null){
                Weather weather = Utility.handleWeatherResponse(secondWeather);
                weatherId = weather.basic.weatherId;
                return WeatherFragment.newInstance(weatherId, position);
            }else {
                return new ChooseAreaFragment();
            }
        }else if (position == 2){
            if (thirdWeather != null){
                Weather weather = Utility.handleWeatherResponse(thirdWeather);
                weatherId = weather.basic.weatherId;
                return WeatherFragment.newInstance(weatherId, position);
            }else {
                return new ChooseAreaFragment();
            }
        }else {
            if (firstWeather != null){
                Weather weather = Utility.handleWeatherResponse(firstWeather);
                weatherId = weather.basic.weatherId;
                return WeatherFragment.newInstance(weatherId, position);
            }else {
                weatherId = ((Activity)context).getIntent().getStringExtra("weather_id");
                return WeatherFragment.newInstance(weatherId, position);
            }
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return 3;
    }
}
