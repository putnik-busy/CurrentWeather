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

public class DataBaseHelper extends SQLiteOpenHelper implements DatabaseHandler {

    static final String dbName = "cityDB";
    static final String list_cityTable = "City";
    //rows
    static final String keyID = "keyID";
    static final String nameCity = "nameCity";
    static final String lonCity = "lonCity";
    static final String latCity = "latCity";
    static final String countryCity = "countryCity";
    static final String sunriseCity = "sunriseCity";
    static final String sunsetCity = "sunsetCity";
    static final String idWeather = "idWeather";
    static final String description = "description";
    static final String main = "main";
    static final String icon = "icon";
    static final String humidity = "humidity";
    static final String pressure = "pressure";
    static final String temp_max = "temp_max";
    static final String temp_min = "temp_min";
    static final String temp = "temp";
    static final String speed = "speed";
    static final String deg = "deg";
    static final String all = "allClouds";
    static final String sIconArray = "iconArray";
    static final int DATABASE_VERSION = 1;

    public interface DatabaseHand<T> {
        void onComplete(boolean success, T result);
    }

    private static abstract class DatabaseAsyncTask<T> extends AsyncTask<Void, Void, T> {
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
        super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CITY_TABLE = "CREATE TABLE " + list_cityTable + "(" +
                keyID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                nameCity + " TEXT," + lonCity + " TEXT," + latCity + " TEXT," +
                countryCity + " TEXT," + sunriseCity + " TEXT," + sunsetCity + " TEXT," +
                idWeather + " TEXT," + description + " TEXT," + main + " TEXT," +
                icon + " TEXT," + humidity + " TEXT," + pressure + " TEXT," +
                temp_max + " REAL," + temp_min + " REAL," + temp + " REAL," +
                speed + " TEXT," + deg + " TEXT," + all + " TEXT," + sIconArray + " BLOB" + ")";
        db.execSQL(CREATE_CITY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + list_cityTable);
        onCreate(db);
    }

    @Override
    public void addCity(Weather weather) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(nameCity, weather.getNameCity());
        db.insert(list_cityTable, null, values);
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

    @Override
    public void addListCity(String[] name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            if (db != null) {
                db.beginTransaction();
                for (String cityName : name) {
                    values.put(nameCity, cityName);
                    db.insertOrThrow("City", nameCity, values);
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

    @Override
    public List<Weather> getAllCity() {
        List<Weather> weatherList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + list_cityTable;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Weather weather = new Weather();
                weather.setCityID(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.keyID)));
                weather.location.setCity(cursor.getString(cursor.getColumnIndex(DataBaseHelper.nameCity)));
                weather.location.setLongitude(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.lonCity)));
                weather.location.setLatitude(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.latCity)));
                weather.location.setCountry(cursor.getString(cursor.getColumnIndex(DataBaseHelper.countryCity)));
                weather.location.setSunrise(cursor.getLong(cursor.getColumnIndex(DataBaseHelper.sunriseCity)));
                weather.location.setSunset(cursor.getLong(cursor.getColumnIndex(DataBaseHelper.sunsetCity)));
                weather.currentCondition.setWeatherId(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.idWeather)));
                weather.currentCondition.setDescr(cursor.getString(cursor.getColumnIndex(DataBaseHelper.description)));
                weather.currentCondition.setCondition(cursor.getString(cursor.getColumnIndex(DataBaseHelper.main)));
                weather.currentCondition.setIcon(cursor.getString(cursor.getColumnIndex(DataBaseHelper.icon)));
                weather.currentCondition.setHumidity(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.humidity)));
                weather.currentCondition.setPressure(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.pressure)));
                weather.temperature.setMaxTemp(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.temp_max)));
                weather.temperature.setMinTemp(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.temp_min)));
                weather.temperature.setTemp(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.temp)));
                weather.wind.setSpeed(cursor.getFloat(cursor.getColumnIndex(speed)));
                weather.wind.setDeg(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.deg)));
                weather.clouds.setPerc(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.all)));
                weather.setIconArray(cursor.getBlob(cursor.getColumnIndex(DataBaseHelper.sIconArray)));
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

    @Override
    public int updateCity(Weather weather) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(nameCity, weather.location.getCity());
        values.put(lonCity, weather.location.getLongitude());
        values.put(latCity, weather.location.getLatitude());
        values.put(countryCity, weather.location.getCountry());
        values.put(sunriseCity, weather.location.getSunrise());
        values.put(sunsetCity, weather.location.getSunset());
        values.put(idWeather, weather.currentCondition.getWeatherId());
        values.put(description, weather.currentCondition.getDescr());
        values.put(main, weather.currentCondition.getCondition());
        values.put(icon, weather.currentCondition.getIcon());
        values.put(humidity, weather.currentCondition.getHumidity());
        values.put(pressure, weather.currentCondition.getPressure());
        values.put(temp_max, weather.temperature.getMaxTemp());
        values.put(temp_min, weather.temperature.getMinTemp());
        values.put(temp, weather.temperature.getTemp());
        values.put(speed, weather.wind.getSpeed());
        values.put(deg, weather.wind.getDeg());
        values.put(all, weather.clouds.getPerc());
        values.put(sIconArray, weather.getIconArray());
        return db.update(list_cityTable, values, keyID + " =?",
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

    @Override
    public void deleteCity(Weather weather) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(list_cityTable, keyID + " = ?",
                new String[]{String.valueOf(weather.getCityID())});
        db.close();
    }

    @Override
    public Weather getCityWeather(long id) {
        Weather weather = new Weather();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + list_cityTable + " where " + keyID + "=" + id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                weather.setCityID(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.keyID)));
                weather.location.setCity(cursor.getString(cursor.getColumnIndex(DataBaseHelper.nameCity)));
                weather.location.setLongitude(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.lonCity)));
                weather.location.setLatitude(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.latCity)));
                weather.location.setCountry(cursor.getString(cursor.getColumnIndex(DataBaseHelper.countryCity)));
                weather.location.setSunrise(cursor.getLong(cursor.getColumnIndex(DataBaseHelper.sunriseCity)));
                weather.location.setSunset(cursor.getLong(cursor.getColumnIndex(DataBaseHelper.sunsetCity)));
                weather.currentCondition.setWeatherId(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.idWeather)));
                weather.currentCondition.setDescr(cursor.getString(cursor.getColumnIndex(DataBaseHelper.description)));
                weather.currentCondition.setCondition(cursor.getString(cursor.getColumnIndex(DataBaseHelper.main)));
                weather.currentCondition.setIcon(cursor.getString(cursor.getColumnIndex(DataBaseHelper.icon)));
                weather.currentCondition.setHumidity(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.humidity)));
                weather.currentCondition.setPressure(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.pressure)));
                weather.temperature.setMaxTemp(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.temp_max)));
                weather.temperature.setMinTemp(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.temp_min)));
                weather.temperature.setTemp(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.temp)));
                weather.wind.setSpeed(cursor.getFloat(cursor.getColumnIndex(speed)));
                weather.wind.setDeg(cursor.getFloat(cursor.getColumnIndex(DataBaseHelper.deg)));
                weather.clouds.setPerc(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.all)));
                weather.setIconArray(cursor.getBlob(cursor.getColumnIndex(DataBaseHelper.sIconArray)));
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
