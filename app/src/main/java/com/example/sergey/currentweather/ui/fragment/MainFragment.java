package com.example.sergey.currentweather.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sergey.currentweather.R;
import com.example.sergey.currentweather.app.JSONWeatherParser;
import com.example.sergey.currentweather.app.MyApplication;
import com.example.sergey.currentweather.db.DataBaseHelper;
import com.example.sergey.currentweather.model.Weather;
import com.example.sergey.currentweather.ui.adapter.CityListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    private List<Weather> weatherList;
    CityListAdapter cityListAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    final String CITY_TEMP_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    final String API_KEY = "&appid=9b4c3bc3f65172d10f361ec0e1e1f2e4";
    final String METRIC = "&units=metric";
    final String IMG_URL = "http://openweathermap.org/img/w/";

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        weatherList = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        cityListAdapter = new CityListAdapter(getActivity(), weatherList);
        recyclerView.setAdapter(cityListAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getCityTemp();
            }
        });
        return rootView;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getCityTemp();
    }

    private void getCityTemp() {
        weatherList.clear();
        swipeRefreshLayout.setRefreshing(true);
        MyApplication.getInstance().getDb().getAllCityAsync(new DataBaseHelper.DatabaseHand<List<Weather>>() {
            @Override
            public void onComplete(boolean success, final List<Weather> result) {
                if (success) {
                    for (final Weather cn : result) {
                        String url = CITY_TEMP_URL + cn.location.getCity() + METRIC + API_KEY;
                        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    MyApplication.getInstance().setWeather(JSONWeatherParser.getWeather(jsonObject.toString(), cn.getId()));
                                    weatherList.add(MyApplication.getInstance().getWeather());
                                    cityListAdapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                            }
                        });
                        MyApplication.getInstance().addToRequestQueue(request, cn.getId());
                    }
                }
                MyApplication.getInstance().getDb().close();
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }

    private void read() {
        MyApplication.getInstance().getDb().getAllCityAsync(new DataBaseHelper.DatabaseHand<List<Weather>>() {
            @Override
            public void onComplete(boolean success, List<Weather> result) {
                if (success) {
                    for (Weather cn : result) {
                        String log =
                                "Id: " + cn.getId() + "\n" +
                                        " , name_city: " + cn.location.getCity() + "\n" +
                                        " , temp_city: " + cn.temperature.getTemp() + "\n";
                        Log.d("TAG", log);
                    }
                }
                MyApplication.getInstance().getDb().close();
            }
        });
    }

    public void saveData() {
        for (Weather weather : weatherList) {
            MyApplication.getInstance().getDb().updateCityAsync(weather,
                    new DataBaseHelper.DatabaseHand<Integer>() {
                        @Override
                        public void onComplete(boolean success, Integer result) {
                            if (success) {
                                read();
                            }
                        }
                    });
        }
    }
}
