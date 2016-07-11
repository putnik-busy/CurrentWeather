package com.example.sergey.currentweather.ui.adapter;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sergey.currentweather.R;
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
        holder.name_city.setTag(weather.getCityID());
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
            read((long) v.getTag());
        }
    }

    private void read(long pos) {
        Bundle args = new Bundle();
        args.putLong("position", pos);
        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(args);
        ft.replace(R.id.container, detailFragment);
        ft.addToBackStack("detail");
        ft.commit();
    }

    public void addItem(Weather weather) {
        this.data.add(weather);
      //  final int size = this.data.size();
      //  notifyItemInserted(size);
        notifyDataSetChanged();
    }

    public void addAll(List<Weather> items) {
        final int size = this.data.size();
        this.data.addAll(items);
        notifyItemRangeInserted(size, items.size());
    }

    public Weather removeItem(int position) {
        final int size = this.data.size();
        Weather weather = data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, size);
        return weather;
    }
}
