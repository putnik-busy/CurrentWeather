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
    static final String keyID = "id";
    static final String nameCity = "nameCity";
    static final String tempCity = "tempCity";
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
                keyID + " INTEGER PRIMARY KEY AUTOINCREMENT," + nameCity + " TEXT," +
                tempCity + " REAL" + ")";
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

        values.put(nameCity, weather.location.getCity());
        values.put(tempCity, weather.temperature.getTemp());

        long id = db.insert(list_cityTable, null, values);
        weather.setId(id);
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
                weather.setId(cursor.getInt(0));
                weather.location.setCity(cursor.getString(1));
                weather.temperature.setTemp(cursor.getFloat(2));
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
        values.put(tempCity, weather.temperature.getTemp());
        return db.update(list_cityTable, values, keyID + " =?",
                new String[]{String.valueOf(weather.getId())});
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
                new String[]{String.valueOf(weather.getId())});
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
                weather.setId(cursor.getInt(0));
                weather.location.setCity(cursor.getString(1));
                weather.temperature.setTemp(cursor.getFloat(2));
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
