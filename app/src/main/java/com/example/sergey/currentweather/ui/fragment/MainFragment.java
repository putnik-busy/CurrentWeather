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


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sergey.currentweather.R;
import com.example.sergey.currentweather.app.JSONWeatherParser;
import com.example.sergey.currentweather.app.MyApplication;
import com.example.sergey.currentweather.db.DataBaseHelper;
import com.example.sergey.currentweather.dialog.MaterialSimpleDialog;
import com.example.sergey.currentweather.model.Weather;
import com.example.sergey.currentweather.ui.activity.DetailActivity;
import com.example.sergey.currentweather.ui.adapter.CityListAdapter;
import com.example.sergey.currentweather.ui.adapter.CityListAdapterClickListener;
import com.example.sergey.currentweather.utils.CustomAnimationUtils;
import com.example.sergey.currentweather.utils.SimpleHorizontalDivider;
import com.example.sergey.currentweather.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, CityListAdapterClickListener {

    final String CITY_TEMP_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    final String API_KEY = "&appid=9b4c3bc3f65172d10f361ec0e1e1f2e4";
    final String METRIC = "&units=metric";
    final String LANG = "&lang=ru";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout mCoordinatorLayout;
    private List<Weather> mWeatherList;
    private CityListAdapter mCityListAdapter;
    private Paint mPoint;
    private ProgressDialog mProgressDialog;
    private FloatingActionButton fab;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWeatherList = new ArrayList<>();
        mCityListAdapter = new CityListAdapter(getActivity(), this, mWeatherList);
        mRecyclerView.addItemDecoration(new SimpleHorizontalDivider(getActivity()));
        mRecyclerView.setAdapter(mCityListAdapter);
        initSwipe();
        setupDialog();

        if (isConnect() && !MyApplication.getInstance().isSaveInDatabase()) {
            weatherForecast();
        } else {
            loadData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        init(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        CustomAnimationUtils.fromRightToLeftShow(fab, getContext());
        super.onResume();
    }

    @Override
    public void onDestroy() {
        CustomAnimationUtils.fromRightToLeftShow(fab, getContext());
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        if (isConnect()) {
            mSwipeRefreshLayout.setRefreshing(true);
            weatherForecast();
        } else {
            showMessage(R.string.error_network);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * start request to server weather
     */

    private void weatherForecast() {
        mProgressDialog.show();
        mWeatherList.clear();
        MyApplication.getInstance().getDb().getAllCityAsync(new DataBaseHelper
                .DatabaseHand<List<Weather>>() {
            @Override
            public void onComplete(boolean success, final List<Weather> result) {
                if (success) {
                    startRequest(result);
                }
                MyApplication.getInstance().getDb().close();
            }
        });
    }

    /**
     * load data from db
     */
    private void loadData() {
        MyApplication.getInstance().getDb().getAllCityAsync(
                new DataBaseHelper.DatabaseHand<List<Weather>>() {
                    @Override
                    public void onComplete(boolean success, List<Weather> result) {
                        if (success) {
                            mCityListAdapter.addAll(result);
                        }
                        MyApplication.getInstance().getDb().close();
                    }
                });
    }

    /**
     * save data to db
     *
     * @param weather include data city
     */
    public synchronized void saveData(Weather weather) {
        MyApplication.getInstance().getDb().updateCityAsync(weather,
                new DataBaseHelper.DatabaseHand<Integer>() {
                    @Override
                    public void onComplete(boolean success, Integer result) {
                        if (success) {
                            MyApplication.getInstance().setSaveInDatabase(true);
                        }
                    }
                });
    }

    /**
     * delete city from db
     *
     * @param weather include data city
     */

    public synchronized void deleteData(Weather weather) {
        MyApplication.getInstance().getDb().deleteCityAsync(weather,
                new DataBaseHelper.DatabaseHand<Void>() {
                    @Override
                    public void onComplete(boolean success, Void result) {
                    }
                });
    }

    public void init(View rootView) {
        mCoordinatorLayout = (CoordinatorLayout) rootView
                .findViewById(R.id.coordinationLayout);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.color_blue, R.color.color_green,
                R.color.color_yellow, R.color.color_red);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        mPoint = new Paint();
    }

    /**
     * check connect Network
     *
     * @return true is a connection
     */
    protected boolean isConnect() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getActivity().getSystemService(cs);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                inputDialog();
                break;
        }
    }

    public void inputDialog() {
        MaterialSimpleDialog.show("add_city", (AppCompatActivity) getActivity(),
                R.string.add_city_title, R.string.add, R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case -1:
                                dialog.dismiss();
                                if (TextUtils.IsEmptyOrNull(MyApplication.getInstance()
                                        .getCityAdd())) {
                                    if (isConnect()) {
                                        Weather weather = new Weather();
                                        weather.setNameCity(MyApplication.getInstance()
                                                .getCityAdd());
                                        saveNewCity(weather);
                                    } else {
                                        showMessage(
                                                R.string.error_network);
                                    }
                                } else {
                                    Toast.makeText(getContext(),
                                            R.string.error_dialog,
                                            Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case -2:
                                dialog.dismiss();
                        }
                    }
                }
        );
    }

    /**
     * save new add city
     *
     * @param weather include city data
     */
    public void saveNewCity(final Weather weather) {
        MyApplication.getInstance().getDb().addCityAsync(weather,
                new DataBaseHelper.DatabaseHand<Void>() {
                    @Override
                    public void onComplete(boolean success, Void result) {
                        if (success) {
                            weatherForecast();
                        }
                    }
                });
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    deleteData(mCityListAdapter.removeItem(position));
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if (dX < 0) {
                        mPoint.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX,
                                (float) itemView.getTop(), (float) itemView.getRight(),
                                (float) itemView.getBottom());
                        c.drawRect(background, mPoint);
                        icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width,
                                (float) itemView.getTop() + width,
                                (float) itemView.getRight() - width,
                                (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, mPoint);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                        isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void setupDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getContext().getResources().getString(R.string.message_dialog));
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getInstance().cancelPendingRequests("GET");
    }

    /**
     * parse json data
     *
     * @param jsonObject data were obtained from server
     * @param cn         object Weather
     */

    public void parseResponse(JSONObject jsonObject, Weather cn) {
        try {
            mProgressDialog.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            if (jsonObject.toString().contains("Not found city")) {
                showMessage(R.string.error_city_add);
            } else {
                MyApplication.getInstance().setWeather(JSONWeatherParser
                        .getWeather(getContext(), jsonObject.toString(), cn.getCityID()));
                saveData(MyApplication.getInstance().getWeather());
            }
            mCityListAdapter.addItem(MyApplication.getInstance().getWeather());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * start request server
     *
     * @param result city list
     */

    public void startRequest(List<Weather> result) {
        for (final Weather cn : result) {
            String url = CITY_TEMP_URL + cn.location.getCity() + LANG + METRIC + API_KEY;
            JsonObjectRequest request = new JsonObjectRequest(url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            parseResponse(jsonObject, cn);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mProgressDialog.dismiss();
                    mSwipeRefreshLayout.setRefreshing(false);
                    MyApplication.getInstance().cancelPendingRequests("GET");
                    if (volleyError instanceof NoConnectionError) {
                        showMessage(R.string.error_network);
                    } else if (volleyError instanceof TimeoutError) {
                        showMessage(R.string.timeout_network);
                    }
                }
            });
            MyApplication.TAG = "GET";
            request.setRetryPolicy(timeOutRequest());
            MyApplication.getInstance().addToRequestQueue(request);
        }
    }

    public RetryPolicy timeOutRequest() {
        return new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public void showMessage(int resId) {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, resId,
                Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void CityListItemClicked(View v, long position) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("position", position);
        intent.putExtras(bundle);
        startActivity(intent);
        CustomAnimationUtils.fromLeftToRightHide(fab, getContext());
    }
}
