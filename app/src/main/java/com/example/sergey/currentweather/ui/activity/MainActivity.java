package com.example.sergey.currentweather.ui.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.sergey.currentweather.R;
import com.example.sergey.currentweather.app.MyApplication;
import com.example.sergey.currentweather.db.DataBaseHelper;
import com.example.sergey.currentweather.model.Weather;
import com.example.sergey.currentweather.ui.fragment.MainFragment;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recordListCity();
    }

    public void initFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, fragment).commit();
    }

    public void recordListCity() {
        SQLiteDatabase db = MyApplication.getInstance().getDb().getReadableDatabase();
        if (!doesTableExist(db, "City")) {
            saveData();
        } else {
            read();
            initFragment(MainFragment.newInstance());
        }
    }

    public boolean doesTableExist(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen()) {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        if (!cursor.moveToFirst()) {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    public void saveData() {
        String[] citiesNames = getResources().getStringArray(R.array.city);
        MyApplication.getInstance().getDb().addListCityAsync(citiesNames, new DataBaseHelper.DatabaseHand<Void>() {
            @Override
            public void onComplete(boolean success, Void result) {
                if (success) {
                    initFragment(MainFragment.newInstance());
                    read();
                }
            }
        });
    }

    private void read() {
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
