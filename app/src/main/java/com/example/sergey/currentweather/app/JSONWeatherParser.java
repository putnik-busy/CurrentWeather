package com.example.sergey.currentweather.app;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

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
import java.util.Arrays;
import java.util.List;

public class JSONWeatherParser {

    private static RequestQueue requestQueue;

    public static Weather getWeather(FragmentActivity activity, String data, long cityID)
            throws JSONException {
        Weather weather = new Weather();
        JSONObject jObj = new JSONObject(data);
        requestQueue = Volley.newRequestQueue(activity.getApplicationContext());

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

    public static void getImage(final Weather result) {
        if (result!= null) {
            IWeatherImageProvider provider = new WeatherImageProvider();
            provider.getImage(result.currentCondition.getIcon(),
                    requestQueue, new IWeatherImageProvider.WeatherImageListener() {
                        @Override
                        public void onImageReady(Bitmap image) {
                            updateDatabase(convertBitmapIntoByteArray(result, image));
                        }
                    });
        }
    }

    private static void updateDatabase(Weather weather) {
        MyApplication.getInstance().getDb().updateCityAsync(
                weather, new DataBaseHelper.DatabaseHand<Integer>() {
                    @Override
                    public void onComplete(boolean success, Integer result) {
                        if (success) {
                            read();
                        }
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

    private static void read() {
        MyApplication.getInstance().getDb().getAllCityAsync(new DataBaseHelper.DatabaseHand<List<Weather>>() {
            @Override
            public void onComplete(boolean success, List<Weather> result) {
                if (success) {
                    for (Weather cn : result) {
                        String log =
                                "Id: " + cn.getCityID() + "\n" +
                                        " , name_city: " + cn.location.getCity() + "\n" +
                                        " , lon: " + cn.location.getLongitude() + "\n" +
                                        " , lat: " + cn.location.getLatitude() + "\n" +
                                        " , country: " + cn.location.getCountry() + "\n" +
                                        " , sunrise: " + cn.location.getSunrise() + "\n" +
                                        " , sunset: " + cn.location.getSunset() + "\n" +
                                        " , weather_id: " + cn.currentCondition.getWeatherId() + "\n" +
                                        " , description: " + cn.currentCondition.getDescr() + "\n" +
                                        " , main: " + cn.currentCondition.getCondition() + "\n" +
                                        " , icon: " + cn.currentCondition.getIcon() + "\n" +
                                        " , humidity: " + cn.currentCondition.getHumidity() + "\n" +
                                        " , pressure: " + cn.currentCondition.getPressure() + "\n" +
                                        " , temp_max: " + cn.temperature.getMaxTemp() + "\n" +
                                        " , temp_min: " + cn.temperature.getMinTemp() + "\n" +
                                        " , speed: " + cn.wind.getSpeed() + "\n" +
                                        " , deg: " + cn.wind.getDeg() + "\n" +
                                        " , all: " + cn.clouds.getPerc() + "\n" +
                                        " , icon_array: " + Arrays.toString(cn.getIconArray()) + "\n";
                        Log.d("TAG", log);
                    }
                }
                MyApplication.getInstance().getDb().close();
            }
        });
    }
}