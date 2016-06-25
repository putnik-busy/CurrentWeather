package com.example.sergey.currentweather.app;


import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.sergey.currentweather.db.DataBaseHelper;
import com.example.sergey.currentweather.model.Weather;

public class MyApplication extends Application {

    public static final String TAG = MyApplication.class
            .getSimpleName();
    private Weather weather;
    private DataBaseHelper db;

    private RequestQueue mRequestQueue;
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        db = new DataBaseHelper(this);
        weather = new Weather();
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, long id) {
        req.setTag(id);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public DataBaseHelper getDb() {
        return db;
    }

    public void setDb(DataBaseHelper db) {
        this.db = db;
    }
}