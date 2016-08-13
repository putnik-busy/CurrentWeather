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

package com.example.sergey.currentweather.ui.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.sergey.currentweather.R;
import com.example.sergey.currentweather.app.MyApplication;
import com.example.sergey.currentweather.db.DataBaseHelper;
import com.example.sergey.currentweather.ui.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_INDEX = "index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        if (savedInstanceState != null) {
            MyApplication.getInstance().setSaveInDatabase(
                    savedInstanceState.getBoolean(KEY_INDEX));
        }
        recordListCity();
    }

    public void initFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.container, fragment).commit();
    }

    /**
     * check city from database, save if false
     */
    public void recordListCity() {
        SQLiteDatabase db = MyApplication.getInstance().getDb().getReadableDatabase();
        if (!doesTableExist(db, "City")) {
            saveData();
        } else {
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

    /**
     * save city in db
     */

    public void saveData() {
        String[] citiesNames = getResources().getStringArray(R.array.city);
        MyApplication.getInstance().getDb().addListCityAsync(citiesNames, new DataBaseHelper.DatabaseHand<Void>() {
            @Override
            public void onComplete(boolean success, Void result) {
                if (success) {
                    initFragment(MainFragment.newInstance());
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_INDEX, MyApplication.getInstance().isSaveInDatabase());
    }
}
