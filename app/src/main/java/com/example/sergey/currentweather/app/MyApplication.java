/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.sergey.currentweather.app;


import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.sergey.currentweather.db.DataBaseHelper;
import com.example.sergey.currentweather.model.Weather;

public class MyApplication extends Application {

    public static String TAG = MyApplication.class.getSimpleName();
    private Weather mWeather;
    private DataBaseHelper mDb;
    private String mCityAdd;
    private boolean mSaveInDatabase;

    private RequestQueue mRequestQueue;
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mDb = new DataBaseHelper(this);
        mWeather = new Weather();
        mSaveInDatabase = false;
    }

    public synchronized static MyApplication getInstance() {
        return sInstance;
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
        return mWeather;
    }

    public void setWeather(Weather mWeather) {
        this.mWeather = mWeather;
    }

    public DataBaseHelper getDb() {
        return mDb;
    }

    public String getCityAdd() {
        return mCityAdd;
    }

    public void setCityAdd(String mCityAdd) {
        this.mCityAdd = mCityAdd;
    }

    public boolean isSaveInDatabase() {
        return mSaveInDatabase;
    }

    public void setSaveInDatabase(boolean mSaveInDatabase) {
        this.mSaveInDatabase = mSaveInDatabase;
    }
}
