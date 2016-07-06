package com.example.sergey.currentweather.loadIcon;


import android.graphics.Bitmap;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;

public class WeatherImageProvider implements IWeatherImageProvider {
    private static final String IMG_URL = "http://openweathermap.org/img/w/";

    @Override
    public Bitmap getImage(String code, RequestQueue requestQueue, final WeatherImageListener listener) {
        String imageURL = IMG_URL + code + ".png";
        ImageRequest ir = new ImageRequest(imageURL, new Response.Listener<Bitmap>() {

            @Override
            public void onResponse(Bitmap response) {
                if (listener != null)
                    listener.onImageReady(response);
            }
        }, 0, 0, null, null);

        requestQueue.add(ir);
        return null;
    }
}