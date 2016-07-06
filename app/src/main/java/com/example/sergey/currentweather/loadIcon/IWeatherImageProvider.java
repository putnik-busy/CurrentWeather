package com.example.sergey.currentweather.loadIcon;


import android.graphics.Bitmap;

import com.android.volley.RequestQueue;

public interface IWeatherImageProvider {

    Bitmap getImage(String code, RequestQueue requestQueue, WeatherImageListener listener);

    interface WeatherImageListener {
        void onImageReady(Bitmap image);
    }
}