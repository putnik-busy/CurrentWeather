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

package com.example.sergey.currentweather.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.example.sergey.currentweather.model.Weather;

import java.util.ArrayList;
import java.util.List;

/**
 * Save data to database
 */

public class DataBaseHelper extends SQLiteOpenHelper implements DatabaseHandler {

    static final String sDbName = "cityDB";
    static final String sListCityTable = "City";
    static final String sKeyId = "keyId";
    static final String sNameCity = "nameCity";
    static final String sLonCity = "lonCity";
    static final String sLatCity = "latCity";
    static final String sCountryCity = "countryCity";
    static final String sSunriseCity = "sunriseCity";
    static final String sSunsetCity = "sunsetCity";
    static final String sIdWeather = "idWeather";
    static final String sDescription = "description";
    static final String sMain = "main";
    static final String sIcon = "sIcon";
    static final String sHumidity = "humidity";
    static final String sPressure = "pressure";
    static final String sTempMax = "tempMax";
    static final String sTempMin = "tempMin";
    static final String sTemp = "temp";
    static final String sSpeed = "speed";
    static final String sDeg = "deg";
    static final String sAll = "allClouds";
    static final String sIconArray = "iconArray";
    static final int DATABASE_VERSION = 1;

    public interface DatabaseHand<T> {
        void onComplete(boolean success, T result);
    }

    /**
     * async save data in database
     *
     * @param <T> input type
     */

    private abstract class DatabaseAsyncTask<T> extends AsyncTask<Void, Void, T> {
        private DatabaseHand<T> handler;
        private RuntimeException error;

        public DatabaseAsyncTask(DatabaseHand<T> handler) {
            this.handler = handler;
        }

        @Override
        protected T doInBackground(Void... params) {
            try {
                return executeMethod();
            } catch (RuntimeException error) {
                this.error = error;
                return null;
            }
        }

        protected abstract T executeMethod();

        @Override
        protected void onPostExecute(T result) {
            handler.onComplete(error == null, result);
        }
    }

    public DataBaseHelper(Context context) {
        super(context, sDbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CITY_TABLE = "CREATE TABLE " + sListCityTable + "(" +
                sKeyId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                sNameCity + " TEXT," + sLonCity + " TEXT," + sLatCity + " TEXT," +
                sCountryCity + " TEXT," + sSunriseCity + " TEXT," + sSunsetCity + " TEXT," +
                sIdWeather + " TEXT," + sDescription + " TEXT," + sMain + " TEXT," +
                sIcon + " TEXT," + sHumidity + " TEXT," + sPressure + " TEXT," +
                sTempMax + " REAL," + sTempMin + " REAL," + sTemp + " REAL," +
                sSpeed + " TEXT," + sDeg + " TEXT," + sAll + " TEXT," + sIconArray + " BLOB" + ")";
        db.execSQL(CREATE_CITY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + sListCityTable);
        onCreate(db);
    }

    /**
     * add city database
     *
     * @param weather include save city
     */

    @Override
    public void addCity(Weather weather) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(sNameCity, weather.getNameCity());
        db.insert(sListCityTable, null, values);
        db.close();
    }

    public void addCityAsync(final Weather weather, DatabaseHand<Void> handler) {
        new DatabaseAsyncTask<Void>(handler) {
            @Override
            protected Void executeMethod() {
                addCity(weather);
                return null;
            }
        }.execute();
    }

    /**
     * add list city database
     *
     * @param name include array city
     */

    @Override
    public void addListCity(String[] name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            if (db != null) {
                db.beginTransaction();
                for (String cityName : name) {
                    values.put(sNameCity, cityName);
                    db.insertOrThrow("City", sNameCity, values);
                    values.clear();
                }
                db.setTransactionSuccessful();
            }
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public void addListCityAsync(final String[] name, DatabaseHand<Void> handler) {
        new DatabaseAsyncTask<Void>(handler) {
            @Override
            protected Void executeMethod() {
                addListCity(name);
                return null;
            }
        }.execute();
    }

    /**
     * get from database list city
     *
     * @return list city
     */

    @Override
    public List<Weather> getAllCity() {
        List<Weather> weatherList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + sListCityTable;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Weather weather = new Weather();
                weather.setCityID(cursor.getInt(
                        cursor.getColumnIndex(DataBaseHelper.sKeyId)));
                weather.location.setCity(
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.sNameCity)));
                weather.location.setLongitude(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sLonCity)));
                weather.location.setLatitude(cursor.getFloat(
                        cursor.getColumnIndex(DataBaseHelper.sLatCity)));
                weather.location.setCountry(
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.sCountryCity)));
                weather.location.setSunrise(
                        cursor.getLong(cursor.getColumnIndex(DataBaseHelper.sSunriseCity)));
                weather.location.setSunset(
                        cursor.getLong(cursor.getColumnIndex(DataBaseHelper.sSunsetCity)));
                weather.currentCondition.setWeatherId(
                        cursor.getInt(cursor.getColumnIndex(DataBaseHelper.sIdWeather)));
                weather.currentCondition.setDescr(
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.sDescription)));
                weather.currentCondition.setCondition(
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.sMain)));
                weather.currentCondition.setIcon(
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.sIcon)));
                weather.currentCondition.setHumidity(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sHumidity)));
                weather.currentCondition.setPressure(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sPressure)));
                weather.temperature.setMaxTemp(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sTempMax)));
                weather.temperature.setMinTemp(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sTempMin)));
                weather.temperature.setTemp(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sTemp)));
                weather.wind.setSpeed(
                        cursor.getFloat(cursor.getColumnIndex(sSpeed)));
                weather.wind.setDeg(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sDeg)));
                weather.clouds.setPerc(
                        cursor.getInt(cursor.getColumnIndex(DataBaseHelper.sAll)));
                weather.setIconArray(
                        cursor.getBlob(cursor.getColumnIndex(DataBaseHelper.sIconArray)));
                weatherList.add(weather);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return weatherList;
    }

    public void getAllCityAsync(DatabaseHand<List<Weather>> handler) {
        new DatabaseAsyncTask<List<Weather>>(handler) {
            @Override
            protected List<Weather> executeMethod() {
                return getAllCity();
            }
        }.execute();
    }

    /**
     * update city data
     *
     * @param weather input data
     * @return id
     */

    @Override
    public int updateCity(Weather weather) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(sNameCity, weather.location.getCity());
        values.put(sLonCity, weather.location.getLongitude());
        values.put(sLatCity, weather.location.getLatitude());
        values.put(sCountryCity, weather.location.getCountry());
        values.put(sSunriseCity, weather.location.getSunrise());
        values.put(sSunsetCity, weather.location.getSunset());
        values.put(sIdWeather, weather.currentCondition.getWeatherId());
        values.put(sDescription, weather.currentCondition.getDescr());
        values.put(sMain, weather.currentCondition.getCondition());
        values.put(sIcon, weather.currentCondition.getIcon());
        values.put(sHumidity, weather.currentCondition.getHumidity());
        values.put(sPressure, weather.currentCondition.getPressure());
        values.put(sTempMax, weather.temperature.getMaxTemp());
        values.put(sTempMin, weather.temperature.getMinTemp());
        values.put(sTemp, weather.temperature.getTemp());
        values.put(sSpeed, weather.wind.getSpeed());
        values.put(sDeg, weather.wind.getDeg());
        values.put(sAll, weather.clouds.getPerc());
        values.put(sIconArray, weather.getIconArray());
        return db.update(sListCityTable, values, sKeyId + " =?",
                new String[]{String.valueOf(weather.getCityID())});
    }

    public void updateCityAsync(final Weather weather, DatabaseHand<Integer> handler) {
        new DatabaseAsyncTask<Integer>(handler) {
            @Override
            protected Integer executeMethod() {
                return updateCity(weather);
            }
        }.execute();
    }

    /**
     * delete city from database
     *
     * @param weather input data
     */
    @Override
    public void deleteCity(Weather weather) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(sListCityTable, sKeyId + " = ?",
                new String[]{String.valueOf(weather.getCityID())});
        db.close();
    }

    /**
     * get city by id
     *
     * @param id city
     * @return object Weather model city
     */

    @Override
    public Weather getCityWeather(long id) {
        Weather weather = new Weather();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + sListCityTable + " where " + sKeyId + "=" + id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                weather.setCityID(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.sKeyId)));
                weather.location.setCity(
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.sNameCity)));
                weather.location.setLongitude(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sLonCity)));
                weather.location.setLatitude(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sLatCity)));
                weather.location.setCountry(
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.sCountryCity)));
                weather.location.setSunrise(
                        cursor.getLong(cursor.getColumnIndex(DataBaseHelper.sSunriseCity)));
                weather.location.setSunset(
                        cursor.getLong(cursor.getColumnIndex(DataBaseHelper.sSunsetCity)));
                weather.currentCondition.setWeatherId(
                        cursor.getInt(cursor.getColumnIndex(DataBaseHelper.sIdWeather)));
                weather.currentCondition.setDescr(
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.sDescription)));
                weather.currentCondition.setCondition(
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.sMain)));
                weather.currentCondition.setIcon(
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.sIcon)));
                weather.currentCondition.setHumidity(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sHumidity)));
                weather.currentCondition.setPressure(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sPressure)));
                weather.temperature.setMaxTemp(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sTempMax)));
                weather.temperature.setMinTemp(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sTempMin)));
                weather.temperature.setTemp(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sTemp)));
                weather.wind.setSpeed(
                        cursor.getFloat(cursor.getColumnIndex(sSpeed)));
                weather.wind.setDeg(
                        cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.sDeg)));
                weather.clouds.setPerc(
                        cursor.getInt(cursor.getColumnIndex(DataBaseHelper.sAll)));
                weather.setIconArray(
                        cursor.getBlob(cursor.getColumnIndex(DataBaseHelper.sIconArray)));
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
        return weather;
    }

    public void getCityAsync(final long id, DatabaseHand<Weather> handler) {
        new DatabaseAsyncTask<Weather>(handler) {
            @Override
            protected Weather executeMethod() {
                return getCityWeather(id);
            }
        }.execute();
    }

    public void deleteCityAsync(final Weather weather, DatabaseHand<Void> handler) {
        new DatabaseAsyncTask<Void>(handler) {
            @Override
            protected Void executeMethod() {
                deleteCity(weather);
                return null;
            }
        }.execute();
    }
}
