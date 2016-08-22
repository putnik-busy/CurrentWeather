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

package com.example.sergey.currentweather.ui.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sergey.currentweather.R;
import com.example.sergey.currentweather.app.MyApplication;
import com.example.sergey.currentweather.db.DataBaseHelper;
import com.example.sergey.currentweather.model.Weather;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailFragment extends Fragment {

    TextView tvDescrWeather, tvLocation, tvTemperature, tvTempUnit, tvTempMin,
            tvTempMax, tvHum, tvWindSpeed, tvWindDeg, tvPress,
            tvSunrise, tvSunset, lineTxt;
    ImageView weatherImage;

    /**
     * load and set data view
     * @param position choose position
     */

    public void loadData(long position) {
        MyApplication.getInstance().getDb().getCityAsync(position,
                new DataBaseHelper.DatabaseHand<Weather>() {
                    @Override
                    public void onComplete(boolean success, Weather result) {
                        if (success) {
                            if (result != null && result.getIconArray() != null) {
                                convertImage(result);
                                setDataView(result);
                            }
                        }
                    }
                });
    }

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.detail_fragment, container, false);
        init(v);
        return v;
    }

    /**
     * set data in View
     *
     * @param result include data city
     */
    public void setDataView(Weather result) {
        tvLocation.setText(String.format("%s,%s", result.location.getCity(),
                result.location.getCountry()));
        tvTemperature.setText(String.valueOf(
                Math.round(result.temperature.getTemp())));
        tvTempMin.setText(MessageFormat.format("{0}°C ",
                Math.round(result.temperature.getMinTemp())));
        tvTempMax.setText(MessageFormat.format("{0}°C",
                Math.round(result.temperature.getMaxTemp())));
        tvWindSpeed.setText(MessageFormat.format("{0} км/ч ",
                Math.round(result.wind.getSpeed())));
        tvWindDeg.setText(MessageFormat.format("{0}°",
                Math.round(result.wind.getDeg())));
        tvHum.setText(MessageFormat.format("{0} %",
                Math.round(result.currentCondition.getHumidity())));
        tvPress.setText(MessageFormat.format("{0} мбар - ",
                Math.round(result.currentCondition.getPressure())));
        tvSunrise.setText(convertTime(result.location.getSunrise()));
        tvSunset.setText(convertTime(result.location.getSunset()));
        int resId = getResource(result.temperature.getTemp());
        lineTxt.setBackgroundResource(resId);
        tvTempUnit.setText("°C");
        tvDescrWeather.setText(result.currentCondition.getDescr());
    }

    public void init(View v) {
        tvDescrWeather = (TextView) v.findViewById(R.id.descrWeather);
        lineTxt = (TextView) v.findViewById(R.id.lineTxt);
        tvLocation = (TextView) v.findViewById(R.id.location);
        tvTemperature = (TextView) v.findViewById(R.id.temp);
        tvTempUnit = (TextView) v.findViewById(R.id.tempUnit);
        tvTempMin = (TextView) v.findViewById(R.id.tempMin);
        tvTempMax = (TextView) v.findViewById(R.id.tempMax);
        tvHum = (TextView) v.findViewById(R.id.humidity);
        tvWindSpeed = (TextView) v.findViewById(R.id.windSpeed);
        tvWindDeg = (TextView) v.findViewById(R.id.windDeg);
        tvPress = (TextView) v.findViewById(R.id.pressure);
        weatherImage = (ImageView) v.findViewById(R.id.imgWeather);
        tvSunrise = (TextView) v.findViewById(R.id.sunrise);
        tvSunset = (TextView) v.findViewById(R.id.sunset);
    }

    public String convertTime(long time) {
        long javaTimestamp = time * 1000L;
        Date date = new Date(javaTimestamp);
        return new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date);
    }

    private int getResource(float temp) {
        int resId = 0;
        if (temp < 10)
            resId = R.drawable.line_shape_blue;
        else if (temp >= 10 && temp <= 24)
            resId = R.drawable.line_shape_green;
        else if (temp > 25)
            resId = R.drawable.line_shape_red;
        return resId;
    }

    public void convertImage(final Weather result) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(result.getIconArray(), 0,
                result.getIconArray().length);
        if (bitmap != null) {
            weatherImage.setImageBitmap(bitmap);
        }
    }
}
