package com.example.sergey.currentweather.ui.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sergey.currentweather.R;
import com.example.sergey.currentweather.app.JSONWeatherParser;
import com.example.sergey.currentweather.app.MyApplication;
import com.example.sergey.currentweather.app.SimpleHorizontalDivider;
import com.example.sergey.currentweather.db.DataBaseHelper;
import com.example.sergey.currentweather.dialog.MaterialSimpleDialog;
import com.example.sergey.currentweather.model.Weather;
import com.example.sergey.currentweather.ui.adapter.CityListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener {

    final String CITY_TEMP_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    final String API_KEY = "&appid=9b4c3bc3f65172d10f361ec0e1e1f2e4";
    final String METRIC = "&units=metric";
    final String LANG = "&lang=ru";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<Weather> weatherList;
    private CityListAdapter cityListAdapter;
    private Paint p = new Paint();
    private ProgressDialog mProgressDialog;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        weatherList = new ArrayList<>();
        cityListAdapter = new CityListAdapter(getActivity(), weatherList);
        mRecyclerView.addItemDecoration(new SimpleHorizontalDivider(getActivity()));
        mRecyclerView.setAdapter(cityListAdapter);
        initSwipe();
        setupDialog();

        if (isConnect() && !MyApplication.getInstance().ismSaveInDatabase()) {
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
    public void onRefresh() {
        if (isConnect()) {
            swipeRefreshLayout.setRefreshing(true);
            weatherForecast();
        } else {
            Toast.makeText(getActivity(),
                    R.string.error_network, Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void weatherForecast() {
        mProgressDialog.show();
        weatherList.clear();
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

    private void loadData() {
        MyApplication.getInstance().getDb().getAllCityAsync(
                new DataBaseHelper.DatabaseHand<List<Weather>>() {
                    @Override
                    public void onComplete(boolean success, List<Weather> result) {
                        if (success) {
                            cityListAdapter.addAll(result);
                        }
                        MyApplication.getInstance().getDb().close();
                    }
                });
    }

    public synchronized void saveData(Weather weather) {
        MyApplication.getInstance().getDb().updateCityAsync(weather,
                new DataBaseHelper.DatabaseHand<Integer>() {
                    @Override
                    public void onComplete(boolean success, Integer result) {
                        if (success) {
                            MyApplication.getInstance().setmSaveInDatabase(true);
                        }
                    }
                });
        read();
    }

    public synchronized void deleteData(Weather weather) {
        MyApplication.getInstance().getDb().deleteCityAsync(weather,
                new DataBaseHelper.DatabaseHand<Void>() {
                    @Override
                    public void onComplete(boolean success, Void result) {
                        read();
                    }
                });
    }

    public void init(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.color_blue, R.color.color_green,
                R.color.color_yellow, R.color.color_red);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

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
                R.string.add_city_title, R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Weather weather = new Weather();
                        weather.setNameCity(MyApplication.getInstance().getCityAdd());
                        saveNewCity(weather, dialog);
                    }
                });
    }

    public void saveNewCity(final Weather weather, final DialogInterface dialog) {
        MyApplication.getInstance().getDb().addCityAsync(weather,
                new DataBaseHelper.DatabaseHand<Void>() {
                    @Override
                    public void onComplete(boolean success, Void result) {
                        if (success) {
                            dialog.dismiss();
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
                    deleteData(cityListAdapter.removeItem(position));
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
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX,
                                (float) itemView.getTop(), (float) itemView.getRight(),
                                (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width,
                                (float) itemView.getTop() + width,
                                (float) itemView.getRight() - width,
                                (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
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

    public void parseResponse(JSONObject jsonObject, Weather cn) {
        try {
            MyApplication.getInstance().setWeather(JSONWeatherParser
                    .getWeather(getContext(), jsonObject.toString(), cn.getCityID()));
            saveData(MyApplication.getInstance().getWeather());
            cityListAdapter.addItem(MyApplication.getInstance().getWeather());
            swipeRefreshLayout.setRefreshing(false);
            mProgressDialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
                    volleyError.printStackTrace();
                }
            });
            MyApplication.TAG = "GET";
            MyApplication.getInstance().addToRequestQueue(request);
        }
    }
}
