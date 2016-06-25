package com.example.sergey.currentweather.ui.adapter;


import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sergey.currentweather.R;
import com.example.sergey.currentweather.app.MyApplication;
import com.example.sergey.currentweather.db.DataBaseHelper;
import com.example.sergey.currentweather.model.Weather;
import com.example.sergey.currentweather.ui.fragment.DetailFragment;

import java.text.MessageFormat;
import java.util.List;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.ViewHolder> {

    private List<Weather> data;
    Context context;

    public CityListAdapter(Context context, List<Weather> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Weather weather = data.get(position);
        holder.name_city.setText(weather.location.getCity());
        holder.name_city.setTag(position+1);
        holder.temperature_city.setText(MessageFormat.format("{0}℃",
                Math.round(weather.temperature.getTemp())));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView name_city, temperature_city;

        public ViewHolder(View itemView) {
            super(itemView);
            name_city = (TextView) itemView.findViewById(R.id.name_city);
            temperature_city = (TextView) itemView.findViewById(R.id.temperature_city);
            name_city.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = (int)v.getTag();
            Log.d("TAG", String.valueOf(pos));
            read(pos);
        }
    }

    public void nextFragment() {
        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.container, new DetailFragment());
        ft.addToBackStack("detail");
        ft.commit();
    }

    private void read(long pos) {
        MyApplication.getInstance().getDb().getCityAsync(pos,new DataBaseHelper.DatabaseHand<Weather>() {
            @Override
            public void onComplete(boolean success, Weather result) {
                if (success) {
                    MyApplication.getInstance().setWeather(result);
                    nextFragment();
                }
                MyApplication.getInstance().getDb().close();
            }
        });
    }


}
