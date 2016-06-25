package com.example.sergey.currentweather.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sergey.currentweather.R;
import com.example.sergey.currentweather.app.MyApplication;

public class DetailFragment extends Fragment {

    TextView tvDescrWeather, tvLocation, tvTemperature, tvTempUnit, tvTempMin,
            tvTempMax, tvHum, tvWindSpeed, tvWindDeg, tvPress, tvPressStatus, tvVisibility,
            tvSunrise, tvSunset;
    ImageView weatherImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.detail_fragment, container, false);
        init(v);
        setDataView();
        return v;
    }

    public void setDataView() {
       // int minTemp = Math.round(MyApplication.getInstance().getWeather().temperature.getTemp());
        tvTempMin.setText(String.valueOf(MyApplication.getInstance().getWeather().location.getCity()));
    }

    public void init(View v) {
        tvDescrWeather = (TextView) v.findViewById(R.id.descrWeather);
        tvLocation = (TextView) v.findViewById(R.id.location);
        tvTemperature = (TextView) v.findViewById(R.id.temp);
        tvTempUnit = (TextView) v.findViewById(R.id.tempUnit);
        tvTempMin = (TextView) v.findViewById(R.id.tempMin);
        tvTempMax = (TextView) v.findViewById(R.id.tempMax);
        tvHum = (TextView) v.findViewById(R.id.humidity);
        tvWindSpeed = (TextView) v.findViewById(R.id.windSpeed);
        tvWindDeg = (TextView) v.findViewById(R.id.windDeg);
        tvPress = (TextView) v.findViewById(R.id.pressure);
        tvPressStatus = (TextView) v.findViewById(R.id.pressureStat);
        tvVisibility = (TextView) v.findViewById(R.id.visibility);
        weatherImage = (ImageView) v.findViewById(R.id.imgWeather);
        tvSunrise = (TextView) v.findViewById(R.id.sunrise);
        tvSunset = (TextView) v.findViewById(R.id.sunset);
    }

    /*
    private int getResource(String unit, float val) {
        float temp = convertToC(unit, val);
        int resId = 0;
        if (temp < 10)
            resId = R.drawable.line_shape_blue;
        else if (temp >= 10 && temp <=24)
            resId = R.drawable.line_shape_green;
        else if (temp > 25)
            resId = R.drawable.line_shape_red;

        return resId;

    }*/
}
