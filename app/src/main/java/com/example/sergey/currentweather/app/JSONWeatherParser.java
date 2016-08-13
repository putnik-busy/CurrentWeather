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

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.sergey.currentweather.db.DataBaseHelper;
import com.example.sergey.currentweather.loadIcon.IWeatherImageProvider;
import com.example.sergey.currentweather.loadIcon.WeatherImageProvider;
import com.example.sergey.currentweather.model.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


/**
 * Parse String json data from openWeather
 */
public class JSONWeatherParser {

    private static RequestQueue sRequestQueue;

    /**
     * @param context use to newRequestQueue
     * @param data    include json city
     * @param cityID  id get city
     * @throws JSONException
     * @returnobject Weather model with data about weather
     */
    public static Weather getWeather(Context context, String data, long cityID)
            throws JSONException {
        Weather weather = new Weather();
        if (context != null) {
            JSONObject jObj = new JSONObject(data);
            sRequestQueue = Volley.newRequestQueue(context);

            JSONObject coordObj = getObject("coord", jObj);
            weather.location.setLatitude(getFloat("lat", coordObj));
            weather.location.setLongitude(getFloat("lon", coordObj));

            JSONObject sysObj = getObject("sys", jObj);
            weather.location.setCountry(getString("country", sysObj));
            weather.location.setSunrise(getInt("sunrise", sysObj));
            weather.location.setSunset(getInt("sunset", sysObj));
            weather.location.setCity(getString("name", jObj));

            JSONArray jArr = jObj.getJSONArray("weather");
            JSONObject JSONWeather = jArr.getJSONObject(0);
            weather.currentCondition.setWeatherId(getInt("id", JSONWeather));
            weather.currentCondition.setDescr(getString("description", JSONWeather));
            weather.currentCondition.setCondition(getString("main", JSONWeather));
            weather.currentCondition.setIcon(getString("icon", JSONWeather));

            JSONObject mainObj = getObject("main", jObj);
            weather.currentCondition.setHumidity(getInt("humidity", mainObj));
            weather.currentCondition.setPressure(getInt("pressure", mainObj));
            weather.temperature.setMaxTemp(getFloat("temp_max", mainObj));
            weather.temperature.setMinTemp(getFloat("temp_min", mainObj));
            weather.temperature.setTemp(getFloat("temp", mainObj));

            JSONObject wObj = getObject("wind", jObj);
            weather.wind.setSpeed(getFloat("speed", wObj));
            weather.wind.setDeg(getFloat("deg", wObj));

            JSONObject cObj = getObject("clouds", jObj);
            weather.clouds.setPerc(getInt("all", cObj));
            weather.setCityID(cityID);
            getImage(weather);
        }
        return weather;
    }


    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getJSONObject(tagName);
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float getFloat(String tagName, JSONObject jObj) {
        float temp = 0;
        try {
            temp = (float) jObj.getDouble(tagName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

    /**
     * get icon weather
     *
     * @param result include Weather model city
     */
    public static void getImage(final Weather result) {
        if (result != null) {
            IWeatherImageProvider provider = new WeatherImageProvider();
            provider.getImage(result.currentCondition.getIcon(),
                    sRequestQueue, new IWeatherImageProvider.WeatherImageListener() {
                        @Override
                        public void onImageReady(Bitmap image) {
                            updateDatabase(convertBitmapIntoByteArray(result, image));
                        }
                    });
        }
    }

    /**
     * update database
     *
     * @param weather save in db
     */
    private static void updateDatabase(Weather weather) {
        MyApplication.getInstance().getDb().updateCityAsync(
                weather, new DataBaseHelper.DatabaseHand<Integer>() {
                    @Override
                    public void onComplete(boolean success, Integer result) {
                    }
                });
    }

    private static Weather convertBitmapIntoByteArray(Weather result, Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        result.setIconArray(byteArray);
        return result;
    }
}